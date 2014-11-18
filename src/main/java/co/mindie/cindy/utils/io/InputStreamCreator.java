package co.mindie.cindy.utils.io;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamCreator {

	InputStream createInputStream() throws IOException;

	long getInputStreamLength();

}
