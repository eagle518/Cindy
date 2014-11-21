package co.mindie.cindy.async.task;

public interface AsyncResult<T> {

	T getResult();

	void onComplete(AsyncCompletionHandler<T> completionHandler);

	void onError(AsyncErrorCompletionHandler errorCompletionHandler);

}
