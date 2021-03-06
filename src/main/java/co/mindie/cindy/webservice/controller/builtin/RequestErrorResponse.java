/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller.builtin
// Error.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 4:16:24 PM
////////

package co.mindie.cindy.webservice.controller.builtin;

public class RequestErrorResponse {

	////////////////////////
	// VARIABLES
	////////////////

	private String message;
	private String exception;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public RequestErrorResponse() {

	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public String getMessage() {
		return this.message;
	}

	public String getException() {
		return this.exception;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setException(String exception) {
		this.exception = exception;
	}
}
