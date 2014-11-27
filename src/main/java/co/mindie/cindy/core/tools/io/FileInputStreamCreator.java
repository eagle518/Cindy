package co.mindie.cindy.core.tools.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileInputStreamCreator implements InputStreamCreator {

	////////////////////////
	// VARIABLES
	////////////////

	final private File file;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public FileInputStreamCreator(File file) {
		this.file = file;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public InputStream createInputStream() throws IOException {
		return new FileInputStream(this.file);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public long getInputStreamLength() {
		return this.file.length();
	}

}
