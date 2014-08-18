package co.mindie.cindy.component;

import java.io.Closeable;

import me.corsin.javatools.exception.StackTraceUtils;
import me.corsin.javatools.task.TaskQueue;
import me.corsin.javatools.timer.TimeSpan;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeUtils;

public abstract class AbstractWorker extends CindyComponent implements Worker, Closeable {

	///////////////////
	// VARIABLES
	///////////////////

	private static final Logger LOGGER = Logger.getLogger(AbstractWorker.class);

	private final String WORKER_NAME;
	private final long WAIT_DURATION_MILLIS;
	private final Object LOCK;

	private boolean startOnInit;
	private boolean stopped = false;
	private Thread worker;
	private TaskQueue taskQueue;

	///////////////////
	// CONSTRUCTORS
	///////////////////

	public AbstractWorker(String workerName, TimeSpan waitDuration) {
		this.WORKER_NAME = workerName;
		this.WAIT_DURATION_MILLIS = waitDuration.getTotalMs();
		this.LOCK = new Object();
		this.taskQueue = new TaskQueue();
		this.startOnInit = true;
	}

	///////////////////
	// WORKER LIFECYCLE
	///////////////////

	@Override
	public void init() {
		super.init();
		if (this.startOnInit) {
			this.start();
		}
	}

	@Override
	public void start() {
		this.stopped = false;
		if (this.worker == null) {
			Thread worker = new Thread(() -> {
				this.work();
			}, this.WORKER_NAME + " Thread");
			worker.setDaemon(true);
			this.worker = worker;
			worker.start();
		}
	}

	private void work() {
		while (!this.stopped) {
			long t0 = DateTimeUtils.currentTimeMillis();
			try {
				LOGGER.debug(this.WORKER_NAME + " running.");
				this.run();
				this.taskQueue.flushTasks();
				LOGGER.debug(this.WORKER_NAME + " ran in " + (DateTimeUtils.currentTimeMillis() - t0) + " ms");
			} catch (Exception e) {
				LOGGER.error("An error occurred while running " + this.WORKER_NAME + "\n" + StackTraceUtils.stackTraceToString(e));
			}

			try {
				synchronized (this.LOCK) {
					this.LOCK.wait(this.WAIT_DURATION_MILLIS);
				}
			} catch (InterruptedException e) {
				LOGGER.error("Error while waiting the worker " + this.WORKER_NAME, e);
			}
		}
	}

	@Override
	public void wakeUp() {
		synchronized (this.LOCK) {
			this.LOCK.notify();
		}
	}

	@Override
	public void stop() {
		this.stopped = true;
		this.worker = null;
		this.wakeUp();
	}

	@Override
	public void close() {
		LOGGER.debug(this.WORKER_NAME + " closing.");
		this.stop();
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
}
