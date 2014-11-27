package co.mindie.cindy.core.tools.io;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamCreator {

	InputStream createInputStream() throws IOException;

	long getInputStreamLength();

}
