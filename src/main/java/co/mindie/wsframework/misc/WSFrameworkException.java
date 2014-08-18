/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.misc
// WSFrameworkException.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 4, 2014 at 4:06:16 PM
////////

package co.mindie.wsframework.misc;

public class WSFrameworkException extends RuntimeException {

	////////////////////////
	// VARIABLES
	////////////////

	private static final long serialVersionUID = 9061124011685090166L;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public WSFrameworkException(String message, Throwable innerException) {
		super(message, innerException);
	}

	public WSFrameworkException(String message) {
		super(message);
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
