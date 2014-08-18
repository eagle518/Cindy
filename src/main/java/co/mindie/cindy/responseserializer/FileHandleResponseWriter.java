/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.responseserializer
// FileHandleResponseWriter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on May 7, 2014 at 5:36:40 PM
////////

package co.mindie.cindy.responseserializer;

import co.mindie.cindy.filehandling.IFileHandle;
import me.corsin.javatools.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHandleResponseWriter implements IResponseWriter {

	////////////////////////
	// VARIABLES
	////////////////

	private String contentType;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public FileHandleResponseWriter(String contentType) {
		this.contentType = contentType;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void writeResponse(Object response, OutputStream outputStream) throws IOException {
		if (response instanceof IFileHandle) {
			IFileHandle fileHandle = (IFileHandle) response;

			InputStream inputStream = null;
			try {
				inputStream = fileHandle.getInputStream();
				IOUtils.writeStream(outputStream, inputStream);

			} catch (IOException e) {
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		}
	}

	@Override
	public Long getContentLength(Object response) {
		if (response instanceof IFileHandle) {
			IFileHandle fileHandle = (IFileHandle) response;

			if (fileHandle.getContentLength() != null) {
				return fileHandle.getContentLength();
			}
		}

		return 0L;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
