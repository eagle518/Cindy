/////////////////////////////////////////////////
// Project : Ever WebService
// Package : com.ever.webservice.filehandling
// IFileHandler.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2013 at 9:39:28 PM
////////

package co.mindie.cindy.core.filehandling;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

public interface IFileHandler {

	OutputStream getOutputStream(String file);

	IFileHandle getFileHandle(String file);

	Iterator<IFileHandle> listFiles(String directory) throws IOException;

	InputStream getInputStream(String file) throws IOException;

	byte[] readFile(String file) throws IOException;

	void deleteFile(String file) throws IOException;

	void writeToFile(String file, byte[] data) throws IOException;

	void writeToFile(String file, InputStream inputStream, long length) throws IOException;

}
