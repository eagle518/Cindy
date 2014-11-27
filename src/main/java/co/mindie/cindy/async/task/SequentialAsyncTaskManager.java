package co.mindie.cindy.async.task;

import co.mindie.cindy.async.manager.AsyncTaskManager;
import co.mindie.cindy.async.task.queue.TaskQueue;
import co.mindie.cindy.async.task.queue.ThreadedSequentialTaskQueue;
import co.mindie.cindy.core.annotation.Load;

@Load
public class SequentialAsyncTaskManager extends AsyncTaskManager {

	////////////////////////
	// VARIABLES
	////////////////


	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	@Override
	protected TaskQueue createTaskQueue() {
		return new ThreadedSequentialTaskQueue();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
