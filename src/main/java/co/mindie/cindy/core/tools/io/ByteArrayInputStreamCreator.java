package co.mindie.cindy.core.tools.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ByteArrayInputStreamCreator implements InputStreamCreator {

	////////////////////////
	// VARIABLES
	////////////////

	final private byte[] bytes;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ByteArrayInputStreamCreator(byte[] bytes) {
		this.bytes = bytes;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public InputStream createInputStream() {
		return new ByteArrayInputStream(this.bytes);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public long getInputStreamLength() {
		return this.bytes.length;
	}

}
