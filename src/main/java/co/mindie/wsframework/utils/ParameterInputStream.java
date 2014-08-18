/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.utils
// ParameterInputStream.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Apr 23, 2014 at 1:25:32 PM
////////

package co.mindie.wsframework.utils;

import me.corsin.javatools.misc.NullArgumentException;

import java.io.IOException;
import java.io.InputStream;

public class ParameterInputStream extends InputStream {

	////////////////////////
	// VARIABLES
	////////////////

	final private InputStream inputStream;
	final private int length;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ParameterInputStream(InputStream inputStream, int length) {
		if (inputStream == null) {
			throw new NullArgumentException("inputStream");
		}
		this.inputStream = inputStream;
		this.length = length;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public int read() throws IOException {
		return this.inputStream.read();
	}

	public int available() throws IOException {
		return this.inputStream.available();
	}

	public void close() throws IOException {
		this.inputStream.close();
	}

	public void mark(int readlimit) {
		this.inputStream.mark(readlimit);
	}

	public void reset() throws IOException {
		this.inputStream.reset();
	}

	public boolean markSupported() {
		return this.inputStream.markSupported();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public int length() {
		return this.length;
	}
}
