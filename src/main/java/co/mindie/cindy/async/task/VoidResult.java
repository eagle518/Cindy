package co.mindie.cindy.async.task;

/**
 * Created by simoncorsin on 20/11/14.
 */
public class VoidResult implements AsyncResult<Void> {

	////////////////////////
	// VARIABLES
	////////////////


	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public Void getResult() {
		return null;
	}

	@Override
	public void onComplete(AsyncCompletionHandler<Void> completionHandler) {
		completionHandler.onCompleted(null);
	}

	@Override
	public void onError(AsyncErrorCompletionHandler errorCompletionHandler) {

	}

}
