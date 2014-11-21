package co.mindie.cindy.async.task;

import co.mindie.cindy.async.AsyncTaskManager;
import co.mindie.cindy.async.task.queue.TaskQueue;
import co.mindie.cindy.async.task.queue.ThreadedSequentialTaskQueue;
import co.mindie.cindy.automapping.Load;

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
