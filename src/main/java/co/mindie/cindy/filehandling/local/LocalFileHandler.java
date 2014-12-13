package co.mindie.cindy.filehandling.local;

import co.mindie.cindy.filehandling.IFileHandle;
import co.mindie.cindy.filehandling.IFileHandler;
import me.corsin.javatools.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by simoncorsin on 13/12/14.
 */
public class LocalFileHandler implements IFileHandler {

	////////////////////////
	// VARIABLES
	////////////////

	private String basePath;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public LocalFileHandler() {
		this("");
	}

	public LocalFileHandler(String basePath) {
		this.basePath = basePath;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public OutputStream getOutputStream(String file) throws IOException {
		return new FileOutputStream(this.basePath + file);
	}

	@Override
	public IFileHandle getFileHandle(String file) {
		return new LocalFileHandle(this.basePath + file);
	}

	@Override
	public Iterator<IFileHandle> listFiles(String directory) throws IOException {
		File file = new File(this.basePath + directory);
		List<IFileHandle> fileHandles = new ArrayList<>();
		for (File subFile : file.listFiles()) {
			fileHandles.add(new LocalFileHandle(subFile));
		}

		return fileHandles.iterator();
	}

	@Override
	public InputStream getInputStream(String file) throws IOException {
		return new LocalFileHandle(this.basePath + file).getInputStream();
	}

	@Override
	public byte[] readFile(String file) throws IOException {
		try (InputStream stream = new LocalFileHandle(this.basePath + file).getInputStream()) {
			return IOUtils.readStream(stream);
		}
	}

	@Override
	public void deleteFile(String file) throws IOException {
		new LocalFileHandle(this.basePath + file).delete();
	}

	@Override
	public void writeToFile(String file, byte[] data) throws IOException {
		this.writeToFile(file, new ByteArrayInputStream(data), data.length);
	}

	@Override
	public void writeToFile(String file, InputStream inputStream, long length) throws IOException {
		new LocalFileHandle(this.basePath + file).write(inputStream, length);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
