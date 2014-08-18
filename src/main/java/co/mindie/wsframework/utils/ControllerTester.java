/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.utils
// ControllerManagerTester.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 6:03:30 PM
////////

package co.mindie.wsframework.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import co.mindie.wsframework.WSApplication;
import co.mindie.wsframework.automapping.HttpMethod;
import co.mindie.wsframework.controller.dummy.DummyHttpRequest;
import co.mindie.wsframework.controller.dummy.DummyHttpResponse;
import co.mindie.wsframework.responseserializer.JsonResponseWriter;

public class ControllerTester {

	////////////////////////
	// VARIABLES
	////////////////

	final private WSApplication application;
	final private JsonResponseWriter responseWriter;


	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ControllerTester(WSApplication application) {
		this.application = application;
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

		this.application.getControllerManager().handle(request, response);

//		try {
			return this.responseWriter.getObjectMapper().readValue(outputStream.toByteArray(), outputType);
//		} catch (JsonMappingException mappingException) {
//			WSError error = responseWriter.getObjectMapper().readValue(outputStream.toByteArray(), WSError.class);
//			System.err.println(error.getException() + ": " + error.getMessage());
//			Assert.assertTrue(false);
//			return null;
//		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
