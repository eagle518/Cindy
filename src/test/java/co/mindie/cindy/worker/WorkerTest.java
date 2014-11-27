package co.mindie.cindy.worker;

import me.corsin.javatools.timer.TimeSpan;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class WorkerTest {

	////////////////////////
	// VARIABLES
	////////////////


	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	@Test
	public void worker_wait_completion() {
		final AtomicBoolean finished = new AtomicBoolean();
		final Object lock = new Object();
		final AtomicBoolean workerStarted = new AtomicBoolean();

		AbstractWorker worker = new AbstractWorker("Yolo", new TimeSpan(1f)) {
			@Override
			public void run() {
				synchronized (workerStarted) {
					workerStarted.set(true);
					workerStarted.notifyAll();
				}

				synchronized (lock) {
					try {
						lock.wait();
						finished.set(true);
					} catch (InterruptedException e) { }
				}
			}
		};

		worker.start();

		final AtomicBoolean closerThreadStarted = new AtomicBoolean();
		final AtomicBoolean closeFinished = new AtomicBoolean();

		Thread closerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (closerThreadStarted) {
					closerThreadStarted.set(true);
					closerThreadStarted.notifyAll();
				}

				worker.close();
				closeFinished.set(true);
			}
		});

		synchronized (workerStarted) {
			if (!workerStarted.get()) {
				try {
					workerStarted.wait();
				} catch (InterruptedException e) { }
			}
		}

		// At this point we know that the worker thread is locking on the lock

		closerThread.start();
		synchronized (closerThreadStarted) {
			if (!closerThreadStarted.get()) {
				try {
					closerThreadStarted.wait();
				} catch (InterruptedException e) { }
			}
		}

		// At this point the close method should be called
		synchronized (lock) {
			try {
				lock.wait(50);
			} catch (InterruptedException e) {
			}
		}

		assertEquals(finished.get(), false);
		assertEquals(closeFinished.get(), false);

		synchronized (lock) {
			lock.notifyAll();
		}
		synchronized (lock) {
			try {
				lock.wait(50);
			} catch (InterruptedException e) {
			}
		}

		assertEquals(finished.get(), true);
		assertEquals(closeFinished.get(), true);

		try {
			closerThread.join();
		} catch (InterruptedException e) {}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
