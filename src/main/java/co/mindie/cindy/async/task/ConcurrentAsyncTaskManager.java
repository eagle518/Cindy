package co.mindie.cindy.async.task;

import co.mindie.cindy.async.AsyncTaskManager;
import co.mindie.cindy.async.task.queue.TaskQueue;
import co.mindie.cindy.async.task.queue.ThreadedConcurrentTaskQueue;
import co.mindie.cindy.automapping.Load;

@Load
public class ConcurrentAsyncTaskManager extends AsyncTaskManager {

	////////////////////////
	// VARIABLES
	////////////////

	private int threadCount;

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	@Override
	protected TaskQueue createTaskQueue() {
		return new ThreadedConcurrentTaskQueue(this.threadCount);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
}
