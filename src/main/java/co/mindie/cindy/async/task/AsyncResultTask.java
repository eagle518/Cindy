package co.mindie.cindy.async.task;

import co.mindie.cindy.core.exception.CindyException;

import java.util.ArrayList;
import java.util.List;

public abstract class AsyncResultTask<T> implements AsyncResult<T>, Runnable {

	////////////////////////
	// VARIABLES
	////////////////

	volatile private boolean completed;
	private Object workingLock;
	private Throwable t;
	private T result;

	private Object completionLock;
	private List<AsyncCompletionHandler<T>> asyncCompletionHandlers;

	private Object errorCompletionLock;
	private List<AsyncErrorCompletionHandler> asyncErrorCompletionHandlers;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public AsyncResultTask() {
		this.workingLock = new Object();
		this.completionLock = new Object();
		this.errorCompletionLock = new Object();
	}

	////////////////////////
	// METHODS
	////////////////

	abstract protected T doRun() throws Throwable;

	public void run() {
		synchronized (this.workingLock) {
			if (!this.completed) {
				try {
					this.result = this.doRun();
				} catch(Throwable t) {
					this.t = t;
				}

				this.completed = true;

				if (this.t == null) {
					synchronized (this.completionLock) {
						if (this.asyncCompletionHandlers != null) {
							this.asyncCompletionHandlers.forEach(f -> f.onCompleted(this.result));
						}
					}
				}

				if (this.t != null) {
					synchronized (this.errorCompletionLock) {
						if (this.asyncErrorCompletionHandlers != null) {
							this.asyncErrorCompletionHandlers.forEach(f -> f.onError(this.t));
						}
					}
				}

				this.workingLock.notifyAll();
			}
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public boolean isCompleted() {
		return completed;
	}

	@Override
	public T getResult() {
		if (!this.completed) {
			synchronized (this.workingLock) {
				if (!this.completed) {
					try {
						this.workingLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}

		if (this.t != null) {
			if (this.t instanceof RuntimeException) {
				throw (RuntimeException)this.t;
			} else {
				throw new CindyException("Result failed", t);
			}
		}

		return this.result;
	}

	@Override
	public void onComplete(AsyncCompletionHandler<T> completionHandler) {
		// We avoid locking the lock if we can
		if (this.completed) {
			if (this.t == null) {
				completionHandler.onCompleted(this.result);
			}
		} else {
			synchronized (this.completionLock) {
				if (this.completed) {
					if (this.t == null) {
						completionHandler.onCompleted(this.result);
					}
				} else {
					if (this.asyncCompletionHandlers == null) {
						this.asyncCompletionHandlers = new ArrayList<>();
					}

					this.asyncCompletionHandlers.add(completionHandler);
				}
			}
		}

	}

	@Override
	public void onError(AsyncErrorCompletionHandler errorCompletionHandler) {
		// We avoid locking the lock if we can
		if (this.completed) {
			if (this.t != null) {
				errorCompletionHandler.onError(this.t);
			}
		} else {
			synchronized (this.errorCompletionLock) {
				if (this.completed) {
					if (this.t != null) {
						errorCompletionHandler.onError(this.t);
					}
				} else {
					if (this.asyncErrorCompletionHandlers == null) {
						this.asyncErrorCompletionHandlers = new ArrayList<>();
					}

					this.asyncErrorCompletionHandlers.add(errorCompletionHandler);
				}
			}
		}
	}

}
