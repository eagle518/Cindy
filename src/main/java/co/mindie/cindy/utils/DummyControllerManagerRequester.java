/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.utils
// ControllerManagerTester.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 6:03:30 PM
////////

package co.mindie.cindy.utils;

import co.mindie.cindy.automapping.HttpMethod;
import co.mindie.cindy.controller.dummy.DummyHttpRequest;
import co.mindie.cindy.controller.dummy.DummyHttpResponse;
import co.mindie.cindy.controller.manager.ControllerManager;
import co.mindie.cindy.responseserializer.JsonResponseWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DummyControllerManagerRequester {

	////////////////////////
	// VARIABLES
	////////////////

	final private ControllerManager controllerManager;
	final private JsonResponseWriter responseWriter;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public DummyControllerManagerRequester(ControllerManager controllerManager) {
		this.controllerManager = controllerManager;
		this.responseWriter = new JsonResponseWriter();
	}

	////////////////////////
	// METHODS
	////////////////

	private DummyHttpRequest createRequest(HttpMethod method) {
		DummyHttpRequest request = new DummyHttpRequest();
		request.setMethod(method);
		request.setQueryParameters(new HashMap<>());

		if (method == HttpMethod.POST) {
			request.setBodyParameters(new HashMap<>());
		}

		return request;
	}

	public <T> T getResponse(HttpMethod method, String path, Map<String, String> parameters, Class<T> outputType) throws IOException {
		DummyHttpRequest request = this.createRequest(method);

		if (parameters != null) {
			for (Entry<String, String> entry : parameters.entrySet()) {
				request.getQueryParameters().put(entry.getKey(), new String[] {  entry.getValue() });
			}
		}

		request.setPathInfo(path);
		DummyHttpResponse response = new DummyHttpResponse();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		response.setOutputStream(outputStream);

		this.controllerManager.handle(request, response);

//		try {
			return this.responseWriter.getObjectMapper().readValue(outputStream.toByteArray(), outputType);
//		} catch (JsonMappingException mappingException) {
//			RequestErrorResponse error = responseWriter.getObjectMapper().readValue(outputStream.toByteArray(), RequestErrorResponse.class);
//			System.err.println(error.getException() + ": " + error.getMessage());
//			Assert.assertTrue(false);
//			return null;
//		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
