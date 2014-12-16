package co.mindie.cindy.worker;

import co.mindie.cindy.core.tools.Initializable;
import me.corsin.javatools.exception.StackTraceUtils;
import me.corsin.javatools.task.TaskQueue;
import me.corsin.javatools.timer.TimeSpan;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeUtils;

import java.io.Closeable;

public abstract class AbstractWorker implements Worker, Closeable, Initializable {

	///////////////////
	// VARIABLES
	///////////////////

	private static final Logger LOGGER = Logger.getLogger(AbstractWorker.class);

	private final String workerName;
	private final Object lock;

	private boolean startOnInit;
	private boolean stopped;
	private boolean waitCompletionOnClose;
	private Thread worker;
	private TaskQueue taskQueue;
	private TimeSpan waitDuration;

	///////////////////
	// CONSTRUCTORS
	///////////////////

	public AbstractWorker(String workerName) {
		this(workerName, null);
	}

	public AbstractWorker(String workerName, TimeSpan waitDuration) {
		this.workerName = workerName;
		this.lock = new Object();
		this.taskQueue = new TaskQueue();
		this.startOnInit = true;
		this.waitCompletionOnClose = true;
		this.waitDuration = waitDuration;
	}

	///////////////////
	// WORKER LIFECYCLE
	///////////////////

	@Override
	public void init() {
		if (this.startOnInit) {
			this.start();
		}
	}

	@Override
	public void start() {
		this.stopped = false;
		if (this.worker == null) {
			Thread worker = new Thread(this::work, this.workerName + " Thread");
			worker.setDaemon(true);
			this.worker = worker;
			worker.start();
		}
	}

	private void work() {
		while (!this.stopped) {
			long t0 = DateTimeUtils.currentTimeMillis();
			try {
				LOGGER.trace(this.workerName + " running.");
				this.run();
				this.taskQueue.flushTasks();
				LOGGER.trace(this.workerName + " ran in " + (DateTimeUtils.currentTimeMillis() - t0) + " ms");
			} catch (Exception e) {
				LOGGER.error("An error occurred while running " + this.workerName + "\n" + StackTraceUtils.stackTraceToString(e));
			}

			try {
				synchronized (this.lock) {
					if (!this.stopped) {
						TimeSpan waitDuration = this.getWaitDuration();
						if (waitDuration != null) {
							this.lock.wait(waitDuration.getTotalMs());
						}
					}
				}
			} catch (InterruptedException e) {
				LOGGER.error("Error while waiting the worker " + this.workerName, e);
			}
		}
	}

	@Override
	public void wakeUp() {
		synchronized (this.lock) {
			this.lock.notifyAll();
		}
	}

	@Override
	public void stop() {
		this.stopped = true;
		this.wakeUp();
		Thread thread = this.worker;
		this.worker = null;

		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException ignored) {
			}
		}
	}

	@Override
	public void close() {
		LOGGER.debug(this.workerName + " closing.");
		Thread workerThread = this.worker;
		this.stop();

		if (this.waitCompletionOnClose && workerThread != null) {
			try {
				workerThread.join();
			} catch (InterruptedException ignored) {
			}
		}
	}

	///////////////////
	// GETTERS/SETTERS
	///////////////////

	public boolean isStartOnInit() {
		return this.startOnInit;
	}

	public void setStartOnInit(boolean startOnInit) {
		this.startOnInit = startOnInit;
	}

	public TaskQueue getTaskQueue() {
		return this.taskQueue;
	}

	public boolean isWaitCompletionOnClose() {
		return waitCompletionOnClose;
	}

	public void setWaitCompletionOnClose(boolean waitCompletionOnClose) {
		this.waitCompletionOnClose = waitCompletionOnClose;
	}

	public TimeSpan getWaitDuration() {
		return waitDuration;
	}

	public void setWaitDuration(TimeSpan waitDuration) {
		this.waitDuration = waitDuration;
	}
}
