/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// WSExceptionHandler.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 9, 2014 at 10:39:24 PM
////////

package co.mindie.wsframework.controller;

import co.mindie.wsframework.component.WSComponent;
import co.mindie.wsframework.context.RequestContext;

public abstract class WSExceptionHandler extends WSComponent implements IRequestErrorHandler {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public WSExceptionHandler() {

	}

	////////////////////////
	// METHODS
	////////////////

	public abstract Object handleRequestException(RequestContext context, Throwable exception);

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
