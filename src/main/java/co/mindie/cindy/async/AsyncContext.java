package co.mindie.cindy.async;

/**
 * Created by simoncorsin on 20/11/14.
 */
public enum AsyncContext {

	/**
	 * The tasks will be executed inside an AsyncTaskManager that will be shared within a ComponentBox.
	 */
	SHARED,

	/**
	 * The tasks will be executed inside an AsyncTaskManager that will be created uniquely for this async method.
	 */
	SINGLE

}
