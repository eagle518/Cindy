/////////////////////////////////////////////////
// Project : exiled-masterserver
// Package : com.kerious.exiled.masterserver.api
// Mapped.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 11, 2013 at 3:20:04 PM
////////

package co.mindie.cindy.async;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Async {

	/**
	 * @return the name of the underlying method to call asynchronously.
	 * If empty, this method requires to have "ASync" at the end of the method name
	 * and the underlying method to call will be the same as this one, minus the ASync
	 * at the end. Such as for "@ASync doStuffAsync()", the underlying method will be
	 * assumed to be "doStuff()"
	 */
	String underlyingMethodName() default "";

	/**
	 * @return the AsyncMode to use.
	 */
	AsyncMode mode() default AsyncMode.SEQUENTIAL;

	/**
	 * @return The AsyncContext to use
	 */
	AsyncContext context() default AsyncContext.SHARED;

	/**
	 * If the mode is AsyncMode.CONCURRENT, this defines the number
	 * of threads to use to process this method. This number of threads
	 * is guaranteed to be at least this value, but it can be higher if
	 * another Concurrent Async method uses the same context with a higher
	 * value.
	 * @return the min number of threads to use
	 */
	int minNumberOfThreads() default 2;

	/**
	 * If the mode is AsyncMode.PERIODIC, this defines the time between
	 * each task flush. A task flush will execute every pending task. The
	 * flush time will be guaranteed to be at most this value, but it can
	 * be lower if another Periodic Async method uses the same context with
	 * a lower value.
	 * @return the time between each task flush in milliseconds.
	 */
	long timeMsBetweenFlush() default 0;

}
