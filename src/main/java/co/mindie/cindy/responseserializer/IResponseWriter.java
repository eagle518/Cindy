/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.context
// IResponseSerializer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Feb 24, 2014 at 1:41:36 PM
////////

package co.mindie.cindy.responseserializer;

import java.io.IOException;
import java.io.OutputStream;

public interface IResponseWriter {

	void writeResponse(Object response, OutputStream outputStream) throws IOException;

	String getContentType();

	Long getContentLength(Object response);

}
