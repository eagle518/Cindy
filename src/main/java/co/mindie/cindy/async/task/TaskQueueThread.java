/////////////////////////////////////////////////
// Project : SCJavaTools
// Package : me.corsin.javatools.task
// TaskQueueThread.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 27, 2013 at 11:53:28 AM
////////

package co.mindie.cindy.async.task;

import co.mindie.cindy.async.task.queue.TaskQueue;

public class TaskQueueThread extends Thread {

	////////////////////////
	// VARIABLES
	////////////////
	
	private TaskQueue taskQueue;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public TaskQueueThread(TaskQueue taskQueue, Runnable target, String name) {
		super(target, name);

		this.taskQueue = taskQueue;
		this.setDaemon(true);
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public TaskQueue getTaskQueue() {
		return taskQueue;
	}
}
