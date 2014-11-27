/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.responsewriter
// StringResponseWriter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 24, 2014 at 11:24:06 AM
////////

package co.mindie.cindy.webservice.responsewriter;

import java.io.IOException;
import java.io.OutputStream;

public class StringResponseWriter implements IResponseWriter {

	////////////////////////
	// VARIABLES
	////////////////

	private String str;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringResponseWriter() {

	}

	public StringResponseWriter(String str) {
		this.str = str;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void writeResponse(Object response, OutputStream outputStream)
			throws IOException {
		if (this.str != null) {
			byte[] bytes = this.str.getBytes();
			outputStream.write(bytes);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public String getContentType() {
		return "text/plain";
	}

	@Override
	public Long getContentLength(Object response) {
		return this.str != null ? new Long(this.str.getBytes().length) : null;
	}
}
