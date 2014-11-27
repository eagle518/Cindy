package co.mindie.cindy.async.task;

import co.mindie.cindy.async.manager.AsyncTaskManager;
import co.mindie.cindy.async.task.queue.TaskQueue;
import co.mindie.cindy.async.task.queue.ThreadedPeriodicTaskQueue;
import co.mindie.cindy.core.annotation.Load;
import org.joda.time.Duration;

@Load
public class PeriodicAsyncTaskManager extends AsyncTaskManager {

	////////////////////////
	// VARIABLES
	////////////////

	private long timeMsBetweenFlushs = Long.MAX_VALUE;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	@Override
	protected TaskQueue createTaskQueue() {
		return new ThreadedPeriodicTaskQueue(new Duration(this.timeMsBetweenFlushs));
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public long getTimeMsBetweenFlushs() {
		return timeMsBetweenFlushs;
	}

	public void setTimeMsBetweenFlushs(long timeMsBetweenFlushs) {
		this.timeMsBetweenFlushs = timeMsBetweenFlushs;
	}
}
