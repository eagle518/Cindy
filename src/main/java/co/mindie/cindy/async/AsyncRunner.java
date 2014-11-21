package co.mindie.cindy.async;

import co.mindie.cindy.async.task.AsyncResult;
import co.mindie.cindy.async.task.AsyncResultTask;

import java.lang.reflect.Method;

/**
 * Created by simoncorsin on 20/11/14.
 */
public class AsyncRunner {

	////////////////////////
	// VARIABLES
	////////////////

	final private Method method;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public AsyncRunner(Method method) {
		this.method = method;
	}

	////////////////////////
	// METHODS
	////////////////

	public AsyncResult<?> run(AsyncTaskManager asyncTaskManager, Object instance, Object[] params) {
		AsyncResultTask<?> asyncResultTask = new AsyncResultTask<Object>() {
			@Override
			protected Object doRun() throws Throwable {
				return method.invoke(instance, params);
			}
		};

		asyncTaskManager.enqueueTask(asyncResultTask);

		return asyncResultTask;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
