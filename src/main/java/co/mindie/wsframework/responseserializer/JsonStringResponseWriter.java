/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.responseserializer
// JsonResponseSerializer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Feb 24, 2014 at 1:46:07 PM
////////

package co.mindie.wsframework.responseserializer;

import co.mindie.api.exceptions.APIException;
import co.mindie.api.exceptions.APIResponseCode;
import co.mindie.api.model.Response;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;

public class JsonStringResponseWriter implements IResponseWriter {

	// //////////////////////
	// VARIABLES
	// //////////////

	private boolean indentEnabled;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	// //////////////////////
	// METHODS
	// //////////////

	private static String getString(Object response) {
		Object candidate = response;
		if (response == null) {
			throw new APIException(APIResponseCode.InternalError, "Null response.");
		} else if (response instanceof String) {
			return (String) response;
		} else if (response instanceof Response) {
			candidate = ((Response<?>) response).getElement();
			if (candidate instanceof String)
				return (String) candidate;
		}
		throw new APIException(APIResponseCode.InternalError, "Unable to write the response (not a String: " + candidate.getClass().getSimpleName() + ").");
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	@Override
	public void writeResponse(Object response, OutputStream outputStream) throws IOException {
		String str = getString(response);
		IOUtils.write(str.getBytes(), outputStream);
	}

	public boolean isIndentEnabled() {
		return this.indentEnabled;
	}

	public void setIndentEnabled(boolean indentEnabled) {
		this.indentEnabled = indentEnabled;
	}

	@Override
	public String getContentType() {
		return "application/json";
	}

	@Override
	public Long getContentLength(Object response) {
		return (long) getString(response).getBytes().length;
	}
}
