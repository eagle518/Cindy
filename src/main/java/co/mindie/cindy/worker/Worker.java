/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.component
// Service.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 3, 2014 at 6:48:34 PM
////////

package co.mindie.cindy.worker;

public interface Worker extends Runnable {

	/**
	 * Launches the worker which will periodically execute its business logic.
	 */
	void start();

	/**
	 * Stops all the worker processing.
	 */
	void stop();

	/**
	 * Executes the worker if it was waiting
	 */
	void wakeUp();

}
