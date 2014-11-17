/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller.servlet
// HttpResponseServletImpl.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 12:27:58 PM
////////

package co.mindie.cindy.controller.servlet;

import co.mindie.cindy.controller.manager.HttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class HttpResponseServletImpl implements HttpResponse {

	////////////////////////
	// VARIABLES
	////////////////

	final private HttpServletResponse response;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public HttpResponseServletImpl(HttpServletResponse response) {
		this.response = response;
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public int getStatusCode() {
		return this.response.getStatus();
	}

	@Override
	public void setStatusCode(int statusCode) {
		this.response.setStatus(statusCode);
	}

	@Override
	public void setHeader(String key, String value) {
		this.response.setHeader(key, value);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return this.response.getOutputStream();
	}

	@Override
	public void sendRedirect(String url) throws IOException {
		this.response.sendRedirect(url);
	}
}
