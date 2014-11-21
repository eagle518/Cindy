package co.mindie.cindy.async.task;

public class ObjectResult<T> implements AsyncResult<T> {

	////////////////////////
	// VARIABLES
	////////////////

	private T result;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ObjectResult(T result) {
		this.result = result;
	}

	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public T getResult() {
		return this.result;
	}

	@Override
	public void onComplete(AsyncCompletionHandler<T> completionHandler) {
		completionHandler.onCompleted(this.result);
	}

	@Override
	public void onError(AsyncErrorCompletionHandler errorCompletionHandler) {

	}

}
