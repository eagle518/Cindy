/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.database.handle
// InvalidSessionUsageException.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 15, 2014 at 1:41:29 PM
////////

package co.mindie.wsframework.database.handle;

import co.mindie.wsframework.exception.WSFrameworkException;

public class InvalidSessionUsageException extends WSFrameworkException {

	////////////////////////
	// VARIABLES
	////////////////

	private static final long serialVersionUID = -2699535875584413036L;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public InvalidSessionUsageException(String sessionOpenedStackTrace) {
		super("Usage of the session from an HibernateDatabaseHandle must be done on the same thread.\n"
				+ "\n"
				+ "The session was initially opened here:\n" + sessionOpenedStackTrace);
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
