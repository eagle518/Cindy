package co.mindie.cindy.component;

import co.mindie.cindy.utils.Initializable;
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

    private final String WORKER_NAME;
    private final long WAIT_DURATION_MILLIS;
    private final Object LOCK;

    private boolean startOnInit;
    private boolean stopped;
    private boolean waitCompletionOnClose;
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
        this.waitCompletionOnClose = true;
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
            Thread worker = new Thread(this::work, this.WORKER_NAME + " Thread");
            worker.setDaemon(true);
            this.worker = worker;
            worker.start();
        }
    }

    private void work() {
        while (!this.stopped) {
            long t0 = DateTimeUtils.currentTimeMillis();
            try {
                LOGGER.trace(this.WORKER_NAME + " running.");
                this.run();
                this.taskQueue.flushTasks();
                LOGGER.trace(this.WORKER_NAME + " ran in " + (DateTimeUtils.currentTimeMillis() - t0) + " ms");
            } catch (Exception e) {
                LOGGER.error("An error occurred while running " + this.WORKER_NAME + "\n" + StackTraceUtils.stackTraceToString(e));
            }

            try {
                synchronized (this.LOCK) {
                    if (!this.stopped) {
                        this.LOCK.wait(this.WAIT_DURATION_MILLIS);
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error("Error while waiting the worker " + this.WORKER_NAME, e);
            }
        }
    }

    @Override
    public void wakeUp() {
        synchronized (this.LOCK) {
            this.LOCK.notifyAll();
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
        LOGGER.debug(this.WORKER_NAME + " closing.");
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
}
