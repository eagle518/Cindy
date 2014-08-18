/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.filehandling
// IFileHandle.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on May 7, 2014 at 5:12:27 PM
////////

package co.mindie.wsframework.filehandling;

import java.io.IOException;
import java.io.InputStream;

public interface IFileHandle {

	String path();

	Long getContentLength();

	InputStream getInputStream() throws IOException;

	void write(InputStream inputStream, int streamSize) throws IOException;

	boolean exists();

	boolean delete();


}
