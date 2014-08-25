/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.serveradapter
// EndpointManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 1, 2014 at 4:35:33 PM
////////

package co.mindie.cindy.controllermanager;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.mindie.cindy.automapping.Endpoint;
import co.mindie.cindy.automapping.HttpMethod;
import co.mindie.cindy.component.CindyComponent;
import co.mindie.cindy.context.RequestContext;
import co.mindie.cindy.controller.ControllerEntry;
import co.mindie.cindy.controller.EndpointPathResult;
import co.mindie.cindy.controller.IRequestErrorHandler;
import co.mindie.cindy.controller.RequestHandler;
import co.mindie.cindy.exception.CindyException;
import co.mindie.cindy.modelconverter.IResolverOutput;
import co.mindie.cindy.responseserializer.IResponseWriter;
import co.mindie.cindy.responseserializer.StringResponseWriter;
import co.mindie.cindy.utils.EndpointIndexer;
import me.corsin.javatools.exception.StackTraceUtils;
import me.corsin.javatools.io.IOUtils;
import me.corsin.javatools.misc.ValueHolder;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.log4j.Logger;

import co.mindie.cindy.authorizer.IRequestContextAuthorizer;
import co.mindie.cindy.controller.EndpointEntry;

public class ControllerManager extends CindyComponent {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(ControllerManager.class);
	final private EndpointIndexer getMapping;
	final private EndpointIndexer putMapping;
	final private EndpointIndexer postMapping;
	final private EndpointIndexer deleteMapping;
	final private Map<Class<?>, ControllerEntry> controllers;
	private volatile boolean maintenanceModeEnabled;
	private volatile boolean failOnResolverNotFound;
	private boolean useReusePool;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ControllerManager() {
		this.getMapping = new EndpointIndexer();
		this.putMapping = new EndpointIndexer();
		this.postMapping = new EndpointIndexer();
		this.deleteMapping = new EndpointIndexer();
		this.controllers = new HashMap<>();
		this.useReusePool = true;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void init() {
		super.init();

		for (ControllerEntry controller : this.controllers.values()) {
			for (EndpointEntry endpoint : controller.getEndpoints()) {
				endpoint.init();
			}
		}
	}

	public void addController(Class<?> controllerClass, String basePath) {
		if (!basePath.endsWith("/")) {
			basePath = basePath + "/";
		}

		ControllerEntry controllerEntry = new ControllerEntry(controllerClass, basePath, this.getApplication());

		Method[] methods = controllerClass.getMethods();

		for (Method method : methods) {
			Endpoint mapped = method.getAnnotation(Endpoint.class);

			if (mapped != null) {
				final String path = mapped.path();
				final HttpMethod type = mapped.httpMethod();
				EndpointEntry endpointEntry = new EndpointEntry(controllerEntry, basePath + path, method, mapped);

				controllerEntry.addEndpoint(endpointEntry);

				this.registerMethod(type, endpointEntry);
			}
		}

		this.controllers.put(controllerClass, controllerEntry);
	}

	private void registerMethod(HttpMethod requestType, EndpointEntry endpoint) {
		switch (requestType) {
			case DELETE:
				this.deleteMapping.addEndpoint(endpoint);
				break;
			case GET:
				this.getMapping.addEndpoint(endpoint);
				break;
			case POST:
				this.postMapping.addEndpoint(endpoint);
				break;
			case PUT:
				this.putMapping.addEndpoint(endpoint);
				break;
			default:
				throw new RuntimeException("Unrecognized request type " + requestType.toString());
		}
	}

	private void createResponseWriter(EndpointEntry endpointEntry, RequestContext context) throws Exception {
		Class<?> responseWriterCls = endpointEntry.getResponseWriterClass();

		if (responseWriterCls != void.class) {
			Object responseWriter = responseWriterCls.newInstance();

			if (!(responseWriter instanceof IResponseWriter)) {
				throw new CindyException("ResponseWriterClass does not implement IResponseWriter");
			}

			context.setResponseWriter((IResponseWriter) responseWriter);
		}

		if (context.getResponseWriter() == null) {
			IResponseWriter writer = this.getApplication().getDefaultResponseWriter();

			if (writer == null) {
				throw new CindyException("No compatible response writer found");
			}

			context.setResponseWriter(writer);
		}
	}

	private RequestHandler createRequestHandler(HttpRequest httpRequest, HttpResponse httpResponse, EndpointEntry associatedMethod) {
		RequestHandler requestHandler = associatedMethod.createRequestHandler(this.getApplication(), this.useReusePool);
		RequestContext context = requestHandler.getRequestContext();

		context.setHttpResponse(httpResponse);
		context.setHttpRequest(httpRequest);

		return requestHandler;
	}

	public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
		EndpointPathResult result = this.getEndpointIndexerForMethod(httpRequest.getMethod()).getEndpointPathResult(httpRequest.getPathInfo());

		ValueHolder<RequestHandler> requestHandlerVH = new ValueHolder<>();
		Object response = this.getRequestResponse(httpRequest, httpResponse, result, requestHandlerVH);

		RequestHandler requestHandler = requestHandlerVH.value();
		IResponseWriter responseWriter = this.getApplication().getDefaultResponseWriter();

		if (requestHandler != null && requestHandler.getRequestContext().getResponseWriter() != null) {
			responseWriter = requestHandler.getRequestContext().getResponseWriter();
		} else if (responseWriter == null) {
			responseWriter = new StringResponseWriter("Internal error: no response writer set in the request handler");
		}

		this.sendResponse(httpRequest, httpResponse, responseWriter, response);

		if (requestHandler != null) {
			requestHandler.release(this.useReusePool);
		}
	}

	////////////////////////
	// HANDLERS
	////////////////

	private Object handleRequestException(RequestContext context, Throwable exception) {
		try {
			IRequestErrorHandler exceptionHandler = this.getApplication().getRequestErrorHandler();

			if (exceptionHandler != null) {
				return exceptionHandler.handleRequestException(context, exception);
			}
		} catch (Throwable t) {
			LOGGER.error("An error occured in the ExceptionHandler! " + StackTraceUtils.stackTraceToString(t));
		}

		return null;
	}

	private Object handleEndpointNotFound(HttpRequest servletRequest, HttpResponse response) {
		response.setStatusCode(404);

		IRequestErrorHandler errorHandler = this.getApplication().getRequestErrorHandler();

		if (errorHandler != null) {
			try {
				return errorHandler.handleEndpointNotFound(servletRequest, response);
			} catch (Throwable t) {
				System.err.println("-- REQUEST ERROR HANDLER THROWN AN EXCEPTION --");
				t.printStackTrace();

			}
		}

		return null;
	}

	private Object handleMaintenanceMode(HttpRequest servletRequest, HttpResponse response) {
		response.setStatusCode(410);

		IRequestErrorHandler errorHandler = this.getApplication().getRequestErrorHandler();

		if (errorHandler != null) {
			try {
				return errorHandler.handleMaintenanceMode(servletRequest, response);
			} catch (Throwable t) {
				System.err.println("-- REQUEST ERROR HANDLER THROWN AN EXCEPTION --");
				t.printStackTrace();
			}
		}

		return null;
	}

	private Object handleRequestCreationFailed(HttpRequest servletRequest, HttpResponse response, Throwable e) {
		response.setStatusCode(500);

		IRequestErrorHandler errorHandler = this.getApplication().getRequestErrorHandler();

		if (errorHandler != null) {
			try {
				return errorHandler.handleRequestCreationFailed(servletRequest, response, e);
			} catch (Throwable t) {
				System.err.println("-- REQUEST ERROR HANDLER THROWN AN EXCEPTION --");
				t.printStackTrace();
			}
		}

		return null;
	}

	private Object handleResponseConverterException(RequestContext context, Throwable e) {
		context.getHttpResponse().setStatusCode(500);

		IRequestErrorHandler errorHandler = this.getApplication().getRequestErrorHandler();

		if (errorHandler != null) {
			try {
				return errorHandler.handleResponseConverterException(context, e);
			} catch (Throwable t) {
				System.err.println("-- REQUEST ERROR HANDLER THROWN AN EXCEPTION --");
				t.printStackTrace();
			}
		}

		return null;
	}

	private void handleResponseWritingException(HttpRequest request, IOException e) {
		IRequestErrorHandler errorHandler = this.getApplication().getRequestErrorHandler();

		if (errorHandler != null) {
			try {
				errorHandler.handleResponseWritingException(request, e);
			} catch (Throwable t) {
				System.err.println("-- REQUEST ERROR HANDLER THROWN AN EXCEPTION --");
				t.printStackTrace();
			}
		}
	}

	private EndpointIndexer getEndpointIndexerForMethod(HttpMethod method) {
		switch (method) {
		case DELETE:
			return this.deleteMapping;
		case GET:
			return this.getMapping;
		case POST:
			return this.postMapping;
		case PUT:
			return this.putMapping;
		default:
			throw new CindyException("Unrecognized httpMethod: " + method);
		}
	}

	private Object getRequestResponse(HttpRequest httpRequest, HttpResponse httpResponse, EndpointPathResult result, ValueHolder<RequestHandler> requestHandlerVH) {
		EndpointEntry endpointEntry = result.getEndpointEntry();
		if (endpointEntry == null) {
			return this.handleEndpointNotFound(httpRequest, httpResponse);
		}

		if (this.isMaintenanceModeEnabled()) {
			return this.handleMaintenanceMode(httpRequest, httpResponse);
		}

		RequestHandler requestHandler;
		RequestContext context;

		try {
			requestHandler = this.createRequestHandler(httpRequest, httpResponse, endpointEntry);
			requestHandlerVH.setValue(requestHandler);
			context = requestHandler.getRequestContext();

			this.createResponseWriter(endpointEntry, context);

			this.createParameters(httpRequest, context, result);
		} catch (Throwable e) {
			return this.handleRequestCreationFailed(httpRequest, httpResponse, e);
		}

		Object response = null;
		Throwable thrownException = null;
		boolean shouldResolveOutput = endpointEntry.shouldResolveOutput();
		int options = endpointEntry.getOutputResolverOptions();

		try {
			context.willBegin();

			this.checkAuthorization(context, endpointEntry.getRequiredAuthorizations());

			response = endpointEntry.invoke(requestHandler.getController(), context);
			context.willEnd(null);

			if (context.shouldResolveOutput() != null) {
				shouldResolveOutput = context.shouldResolveOutput();
			}
			if (context.getOutputResolverOptions() != null) {
				options = context.getOutputResolverOptions();
			}
		} catch (Throwable e) {
			thrownException = e;
			shouldResolveOutput = false;
			try {
				context.willEnd(e);
			} catch (Throwable ignored) {
			}
			response = this.handleRequestException(context, e);
		}

		if (shouldResolveOutput && response != null) {
			try {
				IResolverOutput resolverOutput = this.getApplication().getModelConverterManager().getDefaultResolverOutputForInput(response);

				if (resolverOutput != null) {
					response = resolverOutput.createResolversAndResolve(context.getComponentContext(), response, options);
				} else if (this.failOnResolverNotFound) {
					throw new CindyException("No resolver output found for " + response.getClass().getName());
				}
			} catch (Exception e) {
				thrownException = e;
				response = this.handleResponseConverterException(context, e);
			}
		}

		context.didEnd(response, thrownException);

		return response;
	}

	private void sendResponse(HttpRequest httpRequest, HttpResponse httpResponse, IResponseWriter responseWriter, Object response) {
		OutputStream outputStream = null;

		try {
			try {
				httpResponse.setHeader("Content-Type", responseWriter.getContentType());

				Long contentLength = responseWriter.getContentLength(response);
				if (contentLength != null) {
					httpResponse.setHeader("Content-Length", contentLength.toString());
				}
				try {
					outputStream = httpResponse.getOutputStream();

					responseWriter.writeResponse(response, outputStream);
				} catch (ClientAbortException ignored) {
				}
			} catch (IOException e) {
				this.handleResponseWritingException(httpRequest, e);
			}
		} catch (Exception ignored) {

		} finally {
			if (outputStream != null) {
				IOUtils.closeStream(outputStream);
			}
		}
	}

	private void createParameters(HttpRequest httpRequest, RequestContext context, EndpointPathResult result) throws Throwable {
		List<String> pathValues = result.getPathValues();
		EndpointEntry endpoint = result.getEndpointEntry();
		Map<String, String> resources = context.getUrlResources();

		for (int i = 0, count = pathValues.size(); i < count; i++) {
			String pathValue = pathValues.get(i);

			if (pathValue != null) {
				String pathIdentifier = endpoint.getPathIdentifierForIndex(i);
				String key = pathIdentifier;

				String value = pathValue;

				resources.put(key, value);
			}
		}

//		Parameters parameters = new Parameters(httpRequest, resources);
//
//		context.setParameters(parameters);
	}

	protected void checkAuthorization(RequestContext context, String[] requiredAuthorizations) throws Exception {
		IRequestContextAuthorizer authorizer = this.getApplication().getRequestContextAuthorizer();

		if (authorizer != null) {
			authorizer.checkAuthorization(context, requiredAuthorizations);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public boolean isMaintenanceModeEnabled() {
		return this.maintenanceModeEnabled;
	}

	public void setMaintenanceModeEnabled(boolean maintenanceModeEnabled) {
		this.maintenanceModeEnabled = maintenanceModeEnabled;
	}

	public Collection<ControllerEntry> getControllers() {
		return this.controllers.values();
	}

	public boolean isFailOnResolverNotFound() {
		return this.failOnResolverNotFound;
	}

	public void setFailOnResolverNotFound(boolean failOnResolverNotFound) {
		this.failOnResolverNotFound = failOnResolverNotFound;
	}

	public boolean isUseReusePool() {
		return useReusePool;
	}

	public void setUseReusePool(boolean useReusePool) {
		this.useReusePool = useReusePool;
	}
}
