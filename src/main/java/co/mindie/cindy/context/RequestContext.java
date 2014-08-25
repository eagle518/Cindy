/////////////////////////////////////////////////
// Project : webservice
// Package : com.ever.webservice.context
// RequestContext.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 4, 2013 at 3:45:44 PM
////////

package co.mindie.cindy.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.component.CindyComponent;
import co.mindie.cindy.controller.manager.HttpResponse;
import co.mindie.cindy.utils.IFlushable;
import co.mindie.cindy.automapping.Component;
import co.mindie.cindy.automapping.CreationScope;
import co.mindie.cindy.controller.CindyController;
import co.mindie.cindy.controller.manager.HttpRequest;
import co.mindie.cindy.responseserializer.IResponseWriter;

@Component
public class RequestContext extends CindyComponent {

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
	@Wired(required = false, creationScope = CreationScope.NO_CREATION) private CindyController controller;
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
	 * Called before the request get handled by the CindyController
	 */
	public void willBegin() {

	}

	/**
	 * Called after the request has been handled by the CindyController
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
	 * If the pooling is enabled on the associated CindyController, this method is called
	 * before the CindyController gets to its pool
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

	public CindyController getController() {
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
