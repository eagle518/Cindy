/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller.local
// LocalHttpResponse.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 3:34:50 PM
////////

package co.mindie.cindy.webservice.controller.local;

import co.mindie.cindy.webservice.controller.manager.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class LocalHttpResponse implements HttpResponse {

	////////////////////////
	// VARIABLES
	////////////////

	private int statusCode;
	private OutputStream outputStream;
	private Map<String, String> headers;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public LocalHttpResponse() {
		this.headers = new HashMap<>();
		this.statusCode = 200;
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
		this.headers.put(key, value);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public OutputStream getOutputStream() {
		return this.outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
}
