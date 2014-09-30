package co.mindie.cindy.queue;

import co.mindie.cindy.component.AbstractWorker;
import co.mindie.cindy.component.ComponentPool;
import co.mindie.cindy.component.PoolableComponent;
import me.corsin.javatools.task.TaskQueue;
import me.corsin.javatools.task.ThreadedConcurrentTaskQueue;
import me.corsin.javatools.timer.TimeSpan;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by simoncorsin on 29/09/14.
 */
public class WorkDispatcher<DataType, WorkContextType extends WorkContext<DataType>> extends AbstractWorker {

	////////////////////////
	// VARIABLES
	////////////////

	public static final int DEFAULT_MAX_NUMBER_OF_THREADS = 2;

	private static final Logger LOGGER = Logger.getLogger(WorkDispatcher.class);

	private ComponentPool<WorkContextType> componentPool;
	private Class<WorkContextType> workContextTypeClass;
	private int maxNumberOfThreads;
	private TaskQueue workTaskQueue;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public WorkDispatcher(Class<WorkContextType> workContextTypeClass, String workerName, TimeSpan waitDuration) {
		super(workerName, waitDuration);

		this.maxNumberOfThreads = DEFAULT_MAX_NUMBER_OF_THREADS;
		this.workContextTypeClass = workContextTypeClass;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void init() {
		this.componentPool = new ComponentPool<>(this.getApplication(), this.workContextTypeClass, this.getComponentContext(), true);
		this.workTaskQueue = new ThreadedConcurrentTaskQueue(this.maxNumberOfThreads);

		super.init();
	}

	@Override
	public void close() {
		super.close();

		this.workTaskQueue.close();;
	}

	@Override
	public void run() {
		boolean hasItems = true;

		while (hasItems) {
			PoolableComponent<WorkContextType> poolableWorkContext = this.componentPool.obtain();
			boolean shouldReleaseContext = true;

			try {
				WorkContextType workContext = poolableWorkContext.getComponent();
				List<DataType> items = workContext.getQueue().dequeueItems();

				if (items.size() > 0) {
					shouldReleaseContext = false;
					workContext.prepareForProcessing();
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
								} catch (Exception e) {
									try {
										this.onDeleteError(e);
									} catch (Exception e2) {
										LOGGER.error("An exception was thrown in the onDeleteError() exception handler, this is a serious error.", e2);
									}
								}
							}
						} finally {
							poolableWorkContext.release();
						}
					});

				} else {
					hasItems = false;
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
		LOGGER.error("An error was thrown while trying to process the items", e);
	}

	protected void onDeleteError(Exception e) {
		LOGGER.error("An error was thrown while trying to delete the items", e);
	}

	/**
	 * Called when an exception was thrown while trying to dequeue the items
	 * from the queue.
	 * @param e
	 */
	protected void onQueueError(Exception e) {
		LOGGER.error("An error was thrown while trying to dequeue the items", e);
	}

	/**
	 * Whether the WorkDispatcher should delete the item or not.
	 * The default implementation always deletes it.
	 * @param item
	 * @return
	 */
	public boolean shouldDeleteItem(DataType item) {
		return true;
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
