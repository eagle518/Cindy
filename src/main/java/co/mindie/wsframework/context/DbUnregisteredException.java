/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.context
// DbKeyUnregisteredException.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 29, 2014 at 7:11:35 PM
////////

package co.mindie.wsframework.context;

public class DbUnregisteredException extends RuntimeException {

	////////////////////////
	// VARIABLES
	////////////////

	private static final long serialVersionUID = 2301886438426718432L;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public DbUnregisteredException(String dbKey) {
		super("Database with ID [" + dbKey + "] was not registered in the RequestContext");
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
