package co.mindie.cindy.filehandling.local;

import co.mindie.cindy.filehandling.IFileHandle;
import me.corsin.javatools.io.IOUtils;

import java.io.*;

/**
 * Created by simoncorsin on 13/12/14.
 */
public class LocalFileHandle implements IFileHandle {

	////////////////////////
	// VARIABLES
	////////////////

	private File file;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public LocalFileHandle(String filePath) {
		this(new File(filePath));
	}

	public LocalFileHandle(File file) {
		this.file = file;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public String path() {
		return this.file.getAbsolutePath();
	}

	@Override
	public Long getContentLength() {
		return this.file.length();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(this.file);
	}

	@Override
	public void write(InputStream inputStream, long length) throws IOException {
		try (FileOutputStream fileOutputStream = new FileOutputStream(this.file)) {
			IOUtils.writeStream(fileOutputStream, inputStream);
		}
	}

	@Override
	public boolean exists() {
		return this.file.exists();
	}

	@Override
	public boolean delete() {
		return this.file.delete();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
