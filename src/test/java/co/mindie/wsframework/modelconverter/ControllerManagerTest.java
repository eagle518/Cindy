package co.mindie.wsframework.modelconverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import co.mindie.wsframework.WSApplication;
import co.mindie.wsframework.WSApplicationCreator;
import co.mindie.wsframework.automapping.Endpoint;
import co.mindie.wsframework.automapping.HttpMethod;
import co.mindie.wsframework.automapping.Param;
import co.mindie.wsframework.controller.builtin.WSError;
import co.mindie.wsframework.controller.dummy.DummyHttpRequest;
import co.mindie.wsframework.controller.dummy.DummyHttpResponse;
import co.mindie.wsframework.responseserializer.JsonResponseWriter;

import com.fasterxml.jackson.databind.JsonMappingException;

@RunWith(JUnit4.class)
public class ControllerManagerTest {

	public static class ControllerTest {

		@Endpoint(httpMethod = HttpMethod.GET, path = "parameters")
		public void testParameters(String daString, int daInt, @Param(required = false) Integer daInteger) {

		}

		@Endpoint(httpMethod = HttpMethod.POST, path = "add", resolveOutput = false)
		public int testReturnValue(int number, int number2) {
			return number + number2;
		}

	}

	private WSApplication application;

	@Before
	public void setUp() {
		WSApplicationCreator creator = new WSApplicationCreator() {

			@Override
			protected void onLoad(WSApplication application) {
				application.addController(ControllerTest.class, "", false);
			}


		};
		this.application = creator.createApplication();
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

		this.application.getControllerManager().handle(request, response);

		JsonResponseWriter responseWriter = new JsonResponseWriter();
		try {
			return responseWriter.getObjectMapper().readValue(outputStream.toByteArray(), outputType);
		} catch (JsonMappingException mappingException) {
			WSError error = responseWriter.getObjectMapper().readValue(outputStream.toByteArray(), WSError.class);
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

		if (response instanceof WSError) {
			WSError error = (WSError)response;
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