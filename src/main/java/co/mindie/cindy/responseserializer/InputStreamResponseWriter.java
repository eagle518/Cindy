/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.responseserializer
// MediaResponseWriter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on May 5, 2014 at 8:25:42 PM
////////

package co.mindie.cindy.responseserializer;

import me.corsin.javatools.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamResponseWriter implements IResponseWriter {

	////////////////////////
	// VARIABLES
	////////////////

	private String contentType;
	private boolean closeInputStreamAfterUsage;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public InputStreamResponseWriter(String contentType) {
		this(contentType, true);
	}

	public InputStreamResponseWriter(String contentType, boolean closeInputStreamAfterUsage) {
		this.contentType = contentType;
		this.closeInputStreamAfterUsage = closeInputStreamAfterUsage;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void writeResponse(Object response, OutputStream outputStream) throws IOException {
		InputStream inputStream = (InputStream) response;

		try {
			IOUtils.writeStream(outputStream, inputStream);

			if (this.closeInputStreamAfterUsage) {
				inputStream.close();
			}
		} catch (IOException e) {
//			e.printStackTrace();
//			throw e;
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public String getContentType() {
		return this.contentType;
	}

	public boolean isCloseInputStreamAfterUsage() {
		return this.closeInputStreamAfterUsage;
	}

	public void setCloseInputStreamAfterUsage(boolean closeInputStreamAfterUsage) {
		this.closeInputStreamAfterUsage = closeInputStreamAfterUsage;
	}

	@Override
	public Long getContentLength(Object response) {
		return null;
	}

}
