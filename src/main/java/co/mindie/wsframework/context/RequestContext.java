/////////////////////////////////////////////////
// Project : webservice
// Package : com.ever.webservice.context
// RequestContext.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 4, 2013 at 3:45:44 PM
////////

package co.mindie.wsframework.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.mindie.wsframework.automapping.Component;
import co.mindie.wsframework.automapping.CreationScope;
import co.mindie.wsframework.automapping.Wired;
import co.mindie.wsframework.component.WSComponent;
import co.mindie.wsframework.controller.WSController;
import co.mindie.wsframework.controllermanager.HttpRequest;
import co.mindie.wsframework.controllermanager.HttpResponse;
import co.mindie.wsframework.responseserializer.IResponseWriter;
import co.mindie.wsframework.utils.IFlushable;

@Component
public class RequestContext extends WSComponent {

	// //////////////////////
	// VARIABLES
	// //////////////

	private HttpRequest httpRequest;
	private HttpResponse httpResponse;
	private Map<String, String> urlResources;
	private boolean shouldWriteResponse;
	private Boolean shouldResolveOutput;
	private Integer outputResolverOptions;
	@Wired(required = false) private IResponseWriter responseWriter;
	@Wired(required = false, creationScope = CreationScope.NO_CREATION) private WSController controller;
	@Wired(fieldClass = IFlushable.class) List<IFlushable> flushables;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public RequestContext() {
		this.urlResources = new HashMap<>();
		this.shouldWriteResponse = true;
	}

	// //////////////////////
	// METHODS
	// //////////////

	/**
	 * Called before the request get handled by the WSController
	 */
	public void willBegin() {

	}

	/**
	 * Called after the request has been handled by the WSController
	 */
	public void willEnd(Throwable thrownException) {
		if (thrownException == null) {
			this.flush();
		} else {
			this.cancel();
		}
	}

	public void cancel() {
		for (IFlushable flushable : this.flushables) {
			flushable.cancel();
		}
	}

	public void flush() {
		for (IFlushable flushable : this.flushables) {
			flushable.flush();
		}
	}

	/**
	 * Called after the response has been sent.
	 *
	 * @param response
	 * @param thrownException
	 */
	public void didEnd(Object response, Throwable thrownException) {

	}

	/**
	 * If the pooling is enabled on the associated WSController, this method is called
	 * before the WSController gets to its pool
	 */
	public void reset() {
		this.urlResources.clear();
		this.shouldWriteResponse = true;
		this.shouldResolveOutput = null;
		this.outputResolverOptions = null;
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	public IResponseWriter getResponseWriter() {
		return this.responseWriter;
	}

	public void setResponseWriter(IResponseWriter responseWriter) {
		this.responseWriter = responseWriter;
	}

	public boolean shouldWriteResponse() {
		return this.shouldWriteResponse;
	}

	public void setShouldWriteResponse(boolean shouldWriteResponse) {
		this.shouldWriteResponse = shouldWriteResponse;
	}

	public WSController getController() {
		return this.controller;
	}

	public boolean isShouldWriteResponse() {
		return this.shouldWriteResponse;
	}

	public Boolean shouldResolveOutput() {
		return this.shouldResolveOutput;
	}

	public void setShouldResolveOutput(Boolean shouldResolveOutput) {
		this.shouldResolveOutput = shouldResolveOutput;
	}

	public Integer getOutputResolverOptions() {
		return this.outputResolverOptions;
	}

	public void setOutputResolverOptions(Integer outputResolverOptions) {
		this.outputResolverOptions = outputResolverOptions;
	}

	public HttpRequest getHttpRequest() {
		return this.httpRequest;
	}

	public HttpResponse getHttpResponse() {
		return this.httpResponse;
	}

	public void setHttpRequest(HttpRequest request) {
		this.httpRequest = request;
	}

	public void setHttpResponse(HttpResponse response) {
		this.httpResponse = response;
	}

	public Map<String, String> getUrlResources() {
		return this.urlResources;
	}

	public void setUrlResources(Map<String, String> urlResources) {
		this.urlResources = urlResources;
	}
}
