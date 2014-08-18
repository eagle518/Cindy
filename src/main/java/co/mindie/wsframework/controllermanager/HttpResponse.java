/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.serveradapter
// Response.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 1, 2014 at 5:59:56 PM
////////

package co.mindie.wsframework.controllermanager;

import java.io.IOException;
import java.io.OutputStream;

public interface HttpResponse {

	int getStatusCode();

	void setStatusCode(int statusCode);

	void setHeader(String key, String value);

	OutputStream getOutputStream() throws IOException;

	void sendRedirect(String url) throws IOException;

}
