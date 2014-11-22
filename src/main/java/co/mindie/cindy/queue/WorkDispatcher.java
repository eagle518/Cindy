package co.mindie.cindy.queue;

import co.mindie.cindy.automapping.Box;
import co.mindie.cindy.automapping.WiredCore;
import co.mindie.cindy.component.AbstractWorker;
import co.mindie.cindy.component.ComponentMetadataManager;
import co.mindie.cindy.component.ComponentPool;
import co.mindie.cindy.component.PoolableComponent;
import co.mindie.cindy.component.box.ComponentBox;
import me.corsin.javatools.exception.StackTraceUtils;
import me.corsin.javatools.task.ThreadedConcurrentTaskQueue;
import me.corsin.javatools.timer.TimeSpan;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Box(readOnly = false)
public class WorkDispatcher<DataType, WorkContextType extends WorkContext<DataType>> extends AbstractWorker {

	////////////////////////
	// VARIABLES
	////////////////

	public static final int DEFAULT_MAX_NUMBER_OF_THREADS = 2;
	public static final int DEFAULT_PENDING_TASKS_TO_THREAD_RATIO = 4;

	private static final Logger LOGGER = Logger.getLogger(WorkDispatcher.class);

	private ComponentPool<WorkContextType> componentPool;
	private Class<WorkContextType> workContextTypeClass;
	private int maxNumberOfThreads;
	private int maxPendingTasks;
	private ThreadedConcurrentTaskQueue workTaskQueue;
	private AtomicInteger pendingTasks;
	private final String workerName;
	private final Object queueIsEmptyNotifier;
	private boolean queueIsEmpty;

	@WiredCore private ComponentMetadataManager metadataManager;
	@WiredCore private ComponentBox box;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public WorkDispatcher(Class<WorkContextType> workContextTypeClass, String workerName, TimeSpan waitDuration) {
		super(workerName, waitDuration);

		this.queueIsEmptyNotifier = new Object();
		this.queueIsEmpty = false;
		this.workerName = workerName;
		this.maxNumberOfThreads = DEFAULT_MAX_NUMBER_OF_THREADS;
		this.workContextTypeClass = workContextTypeClass;
		this.pendingTasks = new AtomicInteger();
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void init() {
		this.componentPool = new ComponentPool<>(this.metadataManager, this.workContextTypeClass, this.box);
		this.workTaskQueue = new ThreadedConcurrentTaskQueue(this.maxNumberOfThreads);
		this.maxPendingTasks = this.maxNumberOfThreads * DEFAULT_PENDING_TASKS_TO_THREAD_RATIO;

		super.init();
	}

	@Override
	public void close() {
		super.close();

		if (this.isWaitCompletionOnClose()) {
			this.workTaskQueue.waitAllTasks();
		}

		this.workTaskQueue.close();
		this.notifyQueueIsEmpty();
	}

	@Override
	public void run() {
		boolean shouldWork = true;

		while (shouldWork && this.pendingTasks.get() < this.maxPendingTasks) {
			PoolableComponent<WorkContextType> poolableWorkContext = this.componentPool.obtain();
			boolean shouldReleaseContext = true;
			shouldWork = false;

			try {
				WorkContextType workContext = poolableWorkContext.getComponent();
				List<DataType> items = workContext.getQueue().dequeueItems();
				workContext.flush();

				if (items != null && items.size() > 0) {
					shouldWork = true;
					shouldReleaseContext = false;
					workContext.prepareForProcessing();
					this.pendingTasks.incrementAndGet();
					this.workTaskQueue.executeAsync(() -> {
						try {
							boolean shouldDeleteItems = true;
							try {
								workContext.getWorkProcessor().processItems(items);
							} catch (Exception e) {
								shouldDeleteItems = false;
								try {
									this.onProcessError(e);
								} catch (Exception e2) {
									LOGGER.error("An exception was thrown in the onProcessError() exception handler, this is a serious error.", e2);
								}
							}
							if (shouldDeleteItems) {
								try {
									for (DataType item : items) {
										if (this.shouldDeleteItem(item)) {
											workContext.getQueue().deleteItem(item);
										}
									}
									workContext.flush();
								} catch (Exception e) {
									try {
										this.onDeleteError(e);
									} catch (Exception e2) {
										LOGGER.error("An exception was thrown in the onDeleteError() exception handler, this is a serious error.", e2);
									}
								}
							}
						} finally {
							this.pendingTasks.decrementAndGet();
							workContext.cancel();
							poolableWorkContext.release();
						}
					});
				} else {
					this.notifyQueueIsEmpty();
				}
			} catch (Exception e) {
				try {
					this.onQueueError(e);
				} catch (Exception e2) {
					LOGGER.error("An exception was thrown in the onQueueError() exception handler, this is a serious error.", e2);
				}
			}

			if (shouldReleaseContext) {
				poolableWorkContext.release();
			}
		}
	}

	protected void onProcessError(Exception e) {
		LOGGER.error("An error was thrown while trying to process the items in " + this.workerName + "\n" + StackTraceUtils.stackTraceToString(e));
	}

	protected void onDeleteError(Exception e) {
		LOGGER.error("An error was thrown while trying to delete the items in " + this.workerName + "\n" + StackTraceUtils.stackTraceToString(e));
	}

	/**
	 * Called when an exception was thrown while trying to dequeue the items
	 * from the queue.
	 *
	 * @param e
	 */
	protected void onQueueError(Exception e) {
		LOGGER.error("An error was thrown while trying to dequeue the items in " + this.workerName + "\n" + StackTraceUtils.stackTraceToString(e));
	}

	/**
	 * Whether the WorkDispatcher should delete the item or not.
	 * The default implementation always deletes it.
	 *
	 * @param item
	 * @return
	 */
	public boolean shouldDeleteItem(DataType item) {
		return true;
	}

	public void waitWorkCompletion() {
		while (!this.queueIsEmpty) {
			synchronized (this.queueIsEmptyNotifier) {
				try {
					this.queueIsEmptyNotifier.wait();
				} catch (InterruptedException ignored) {
				}
			}
		}
		this.workTaskQueue.waitAllTasks();
	}

	private void notifyQueueIsEmpty() {
		synchronized (this.queueIsEmptyNotifier) {
			this.queueIsEmpty = true;
			this.queueIsEmptyNotifier.notifyAll();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public int getMaxNumberOfThreads() {
		return maxNumberOfThreads;
	}

	public void setMaxNumberOfThreads(int maxNumberOfThreads) {
		this.maxNumberOfThreads = maxNumberOfThreads;
	}
}
