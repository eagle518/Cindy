package co.mindie.cindy.controller.manager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import co.mindie.cindy.AbstractCindyTest;
import co.mindie.cindy.CindyWebApp;
import co.mindie.cindy.CindyWebAppCreator;
import co.mindie.cindy.automapping.*;
import co.mindie.cindy.component.ComponentMetadataManager;
import co.mindie.cindy.resolver.IDynamicResolver;
import co.mindie.cindy.resolver.IResolver;
import co.mindie.cindy.resolver.ResolverContext;
import co.mindie.cindy.resolver.builtin.RequestContextToStringResolver;
import me.corsin.javatools.string.Strings;
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

	public static class Wololo {

		public int value;

	}

	@Resolver(managedInputClasses = Wololo.class, managedOutputClasses = { Integer.class, int.class }, isDefaultForInputTypes = true)
	public static class WololoResolver implements IResolver<Wololo, Integer> {

		@Override
		public Integer resolve(Wololo wololo, Class<?> expectedOutputType, ResolverContext options) {
			return wololo.value;
		}
	}

	@Resolver(managedInputClasses = List.class, managedOutputClasses = List.class, isDefaultForInputTypes = true)
	public static class DynamicResolver implements IDynamicResolver<List, List> {

		private IResolver subResolver;

		@Override
		public void appendSubResolver(IResolver resolver) {
			this.subResolver = resolver;
		}

		@Override
		public List resolve(List list, Class<?> expectedOutputType, ResolverContext options) {
			List output = new ArrayList<>();

			for (Object obj : list) {
				output.add(this.subResolver.resolve(obj, expectedOutputType, options));
			}

			return output;
		}
	}

	@Controller(basePath = "")
	@Load
	public static class ControllerTest {

		@Endpoint(httpMethod = HttpMethod.GET, path = "parameters")
		public void testParameters(String daString, int daInt, @Param(required = false) Integer daInteger) {

		}

		@Endpoint(httpMethod = HttpMethod.POST, path = "add")
		public int testReturnValue(int number, int number2) {
			return number + number2;
		}

		@Endpoint(httpMethod = HttpMethod.PUT, path = "dynamic")
		public List<Wololo> dynamicResolver(int[] values) {

			List<Wololo> list = new ArrayList<>();

			for (int value : values) {
				Wololo wololo = new Wololo();
				wololo.value = value;
				list.add(wololo);
			}

			return list;
		}
	}

	@Wired private CindyWebApp webApp;

	@Override
	protected void onLoad(ComponentMetadataManager metadataManager) {
		super.onLoad(metadataManager);

		metadataManager.loadComponent(RequestContextToStringResolver.class);
		metadataManager.loadComponent(ControllerTest.class);
		metadataManager.loadComponent(WololoResolver.class);
		metadataManager.loadComponent(DynamicResolver.class);
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

	@Test
	public void test_dynamic_resolve() throws IOException {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("values", "1,2,3,4,5,6,7,8,9");

		int[] response = this.getResponse(HttpMethod.PUT, "dynamic", parameters, int[].class);
		Assert.assertArrayEquals(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, response);
	}

}