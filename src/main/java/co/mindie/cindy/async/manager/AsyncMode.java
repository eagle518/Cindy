package co.mindie.cindy.async.manager;

/**
 * Created by simoncorsin on 20/11/14.
 */
public enum AsyncMode {

	/**
	 * Tasks are executed in the order in which they were added but
	 * they can be run concurrently. The number of threads will define
	 * the number of maximum concurrent task that can be run simultaneously.
	 */
	CONCURRENT,

	/**
	 * Tasks are executed in the order in which they were added and are not
	 * run simultaneously. Only one task at a time is processed.
	 */
	SEQUENTIAL,

	/**
	 * Like SEQUENTIAL, but task are all flushed periodically instead of being
	 * run right away. They can be executed for instance once every hour.
	 */
	PERIODIC

}
