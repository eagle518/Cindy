package co.mindie.cindy.controller;

import co.mindie.cindy.automapping.HttpMethod;
import co.mindie.cindy.controller.builtin.RequestErrorResponse;
import co.mindie.cindy.controller.dummy.DummyHttpRequest;
import co.mindie.cindy.controller.dummy.DummyHttpResponse;
import co.mindie.cindy.controller.manager.ControllerManager;
import co.mindie.cindy.responseserializer.JsonResponseWriter;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ControllersTester {

	////////////////////////
	// VARIABLES
	////////////////

	private ControllerManager controllerManager;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ControllersTester(ControllerManager controllerManager) {
		this.controllerManager = controllerManager;
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

		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			request.getQueryParameters().put(entry.getKey(), new String[] {  entry.getValue() });
		}

		request.setPathInfo(path);
		DummyHttpResponse response = new DummyHttpResponse();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		response.setOutputStream(outputStream);

		this.controllerManager.handle(request, response);

		JsonResponseWriter responseWriter = new JsonResponseWriter();
		try {
			return responseWriter.getObjectMapper().readValue(outputStream.toByteArray(), outputType);
		} catch (JsonMappingException mappingException) {
			RequestErrorResponse error = responseWriter.getObjectMapper().readValue(outputStream.toByteArray(), RequestErrorResponse.class);

			throw new IOException("Received error: " + error.getMessage());
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
