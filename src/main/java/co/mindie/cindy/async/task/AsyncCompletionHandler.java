package co.mindie.cindy.async.task;

public interface AsyncCompletionHandler<T> {

	void onCompleted(T result);

}
