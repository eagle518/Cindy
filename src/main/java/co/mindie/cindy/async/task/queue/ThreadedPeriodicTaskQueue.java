/////////////////////////////////////////////////
// Project : SCJavaTools
// Package : me.corsin.javatools.task
// ThreadedPeriodicTaskQueue.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 15, 2014 at 2:54:04 PM
////////

package co.mindie.cindy.async.task.queue;

import co.mindie.cindy.async.task.TaskQueueThread;
import org.joda.time.Duration;

/**
 * TaskQueue that flushes automatically every given time.
 * Tasks are processed sequentially in the same thread.
 * Order in which task are processed are guaranted to be the same that the order in which
 * they were added, one task will start to be processed after the previous one has completed
 * @author simoncorsin
 *
 */
public class ThreadedPeriodicTaskQueue extends TaskQueue implements Runnable {

	////////////////////////
	// VARIABLES
	////////////////

	private Duration durationBetweenFlushs;
	private TaskQueueThread taskQueueThread;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ThreadedPeriodicTaskQueue(Duration durationBetweenFlushs) {
		this.durationBetweenFlushs = durationBetweenFlushs;
		this.taskQueueThread = new TaskQueueThread(this,  this, "PeriodicTaskQueueThread");
		this.taskQueueThread.start();
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void run() {
		while (!this.isClosed()) {
			this.flushTasks();

			Duration duration = this.durationBetweenFlushs;
			long timeToWait = duration != null ? duration.getMillis() : 0;

			try {
				Thread.sleep(timeToWait);
			} catch (InterruptedException e) { }
		}
	}

	@Override
	public void close() {
		super.close();

		if (this.taskQueueThread != null) {
			try {
				this.taskQueueThread.join();
			} catch (InterruptedException e) { }
			this.taskQueueThread = null;
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Duration getDurationBetweenFlushs() {
		return durationBetweenFlushs;
	}

	public void setDurationBetweenFlushs(Duration durationBetweenFlushs) {
		this.durationBetweenFlushs = durationBetweenFlushs;
	}
}
