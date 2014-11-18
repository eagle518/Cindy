/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.utils
// ControllerManagerTester.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 6:03:30 PM
////////

package co.mindie.cindy.controller.local;

import co.mindie.cindy.automapping.HttpMethod;
import co.mindie.cindy.controller.local.LocalHttpRequest;
import co.mindie.cindy.controller.local.LocalHttpResponse;
import co.mindie.cindy.controller.local.LocalResult;
import co.mindie.cindy.controller.manager.ControllerManager;
import co.mindie.cindy.responseserializer.JsonResponseWriter;
import co.mindie.cindy.utils.io.InputStreamCreator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LocalRequester {

	////////////////////////
	// VARIABLES
	////////////////

	final private ControllerManager controllerManager;
	private LocalHttpRequest request;
	private LocalHttpResponse response;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public LocalRequester(ControllerManager controllerManager) {
		this.controllerManager = controllerManager;
		this.request = new LocalHttpRequest();
		this.response = new LocalHttpResponse();
		this.request.setMethod(HttpMethod.GET);
	}

	////////////////////////
	// METHODS
	////////////////

	public LocalRequester path(String path) {
		this.request.setPathInfo(path);

		return this;
	}

	public LocalRequester method(HttpMethod httpMethod) {
		this.request.setMethod(httpMethod);

		return this;
	}

	public LocalRequester queryParameters(Map<String, Object> parameters) {
		for (Entry<String, Object> entry : parameters.entrySet()) {
			this.queryParameter(entry.getKey(), entry.getValue());
		}

		return this;
	}

	public LocalRequester queryParameter(String parameter, Object value) {
		this.request.putQueryParameter(parameter, value);

		return this;
	}

	public LocalRequester bodyParameter(String parameter, String contentType, InputStreamCreator inputStreamCreator) {
		this.request.putBodyParameter(parameter, contentType, inputStreamCreator);

		return this;
	}

	public int result(OutputStream outputStream) {
		this.response.setOutputStream(outputStream);
		this.controllerManager.handle(this.request, this.response);

		return this.response.getStatusCode();
	}

	public <SuccessType, ErrorType> LocalResult<SuccessType, ErrorType> result(Class<SuccessType> successResponseClass, Class<ErrorType> errorResponseClass) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int responseCode = this.result(outputStream);

		JsonResponseWriter responseWriter = new JsonResponseWriter();

		if (responseCode >= 200 && responseCode < 300) {
			return new LocalResult<>(responseWriter.getObjectMapper().readValue(outputStream.toByteArray(), successResponseClass), null);
		} else {
			return new LocalResult<>(null, responseWriter.getObjectMapper().readValue(outputStream.toByteArray(), errorResponseClass));
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
