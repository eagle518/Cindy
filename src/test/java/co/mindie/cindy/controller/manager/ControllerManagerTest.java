package co.mindie.cindy.controller.manager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import co.mindie.cindy.AbstractCindyTest;
import co.mindie.cindy.CindyWebApp;
import co.mindie.cindy.CindyWebAppCreator;
import co.mindie.cindy.automapping.*;
import co.mindie.cindy.component.ComponentMetadataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import co.mindie.cindy.controller.builtin.RequestErrorResponse;
import co.mindie.cindy.controller.dummy.DummyHttpRequest;
import co.mindie.cindy.controller.dummy.DummyHttpResponse;
import co.mindie.cindy.responseserializer.JsonResponseWriter;

import com.fasterxml.jackson.databind.JsonMappingException;

public class ControllerManagerTest extends AbstractCindyTest {

	@Controller(basePath = "")
	@Load
	public static class ControllerTest {

		@Endpoint(httpMethod = HttpMethod.GET, path = "parameters")
		public void testParameters(String daString, int daInt, @Param(required = false) Integer daInteger) {

		}

		@Endpoint(httpMethod = HttpMethod.POST, path = "add", resolveOutput = false)
		public int testReturnValue(int number, int number2) {
			return number + number2;
		}

	}

	@Wired private CindyWebApp webApp;

	@Override
	protected void onLoad(ComponentMetadataManager metadataManager) {
		super.onLoad(metadataManager);

		metadataManager.loadComponent(ControllerTest.class);
	}

	private DummyHttpRequest createRequest(HttpMethod method) {
		DummyHttpRequest request = new DummyHttpRequest();
		request.setMethod(method);
		request.setQueryParameters(new HashMap<>());

		if (method == HttpMethod.POST) {
			request.setBodyParameters(new HashMap<>());
		}

		return request;
	}

	private <T> T getResponse(HttpMethod method, String path, Map<String, String> parameters, Class<T> outputType) throws IOException {
		DummyHttpRequest request = this.createRequest(method);

		for (Entry<String, String> entry : parameters.entrySet()) {
			request.getQueryParameters().put(entry.getKey(), new String[] {  entry.getValue() });
		}

		request.setPathInfo(path);
		DummyHttpResponse response = new DummyHttpResponse();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		response.setOutputStream(outputStream);

		this.webApp.getControllerManager().handle(request, response);

		JsonResponseWriter responseWriter = new JsonResponseWriter();
		try {
			return responseWriter.getObjectMapper().readValue(outputStream.toByteArray(), outputType);
		} catch (JsonMappingException mappingException) {
			RequestErrorResponse error = responseWriter.getObjectMapper().readValue(outputStream.toByteArray(), RequestErrorResponse.class);
			System.err.println(error.getException() + ": " + error.getMessage());
			Assert.assertTrue(false);
			return null;
		}
	}

	@Test
	public void parameters() throws IOException {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("da_string", "that string!");
		parameters.put("da_int", "42");

		Object response = this.getResponse(HttpMethod.GET, "parameters", parameters, Object.class);

		if (response instanceof RequestErrorResponse) {
			RequestErrorResponse error = (RequestErrorResponse)response;
			System.err.println(error.getException() + ": " + error.getMessage());
			Assert.assertTrue(false);
		}
	}

	@Test
	public void test_addition() throws IOException {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("number", "42");
		parameters.put("number2", "1000");

		Integer response = this.getResponse(HttpMethod.POST, "add", parameters, Integer.class);
		Assert.assertEquals(response.longValue(), 1042L);
	}

}