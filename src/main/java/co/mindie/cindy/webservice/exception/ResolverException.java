/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver
// ModelConverterException.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Feb 13, 2014 at 1:22:04 PM
////////

package co.mindie.cindy.webservice.exception;

import co.mindie.cindy.core.exception.CindyException;

public class ResolverException extends CindyException {

	////////////////////////
	// VARIABLES
	////////////////

	private static final long serialVersionUID = 1204913068335367552L;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ResolverException(String message, Exception inner) {
		super(message, inner);
	}

	public ResolverException(String message) {
		super(message);
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
