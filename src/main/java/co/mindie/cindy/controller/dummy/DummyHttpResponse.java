/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller.dummy
// DummyHttpResponse.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 3:34:50 PM
////////

package co.mindie.cindy.controller.dummy;

import java.io.IOException;
import java.io.OutputStream;

import co.mindie.cindy.controller.manager.HttpResponse;

public class DummyHttpResponse implements HttpResponse {

	////////////////////////
	// VARIABLES
	////////////////

	private int statusCode;
	private OutputStream outputStream;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public DummyHttpResponse() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void sendRedirect(String url) throws IOException {

	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public int getStatusCode() {
		return this.statusCode;
	}

	@Override
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	@Override
	public void setHeader(String key, String value) {

	}

	@Override
	public OutputStream getOutputStream() {
		return this.outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
}
