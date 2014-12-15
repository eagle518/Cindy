/////////////////////////////////////////////////
// Project : webservice
// Package : com.ever.webservice.context
// RequestContext.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 4, 2013 at 3:45:44 PM
////////

package co.mindie.cindy.context;

import co.mindie.cindy.automapping.Load;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.controller.manager.HttpRequest;
import co.mindie.cindy.controller.manager.HttpResponse;
import co.mindie.cindy.exception.BadParameterException;
import co.mindie.cindy.responseserializer.IResponseWriter;
import co.mindie.cindy.utils.Cancelable;
import co.mindie.cindy.utils.Flushable;
import org.apache.commons.fileupload.FileItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Load(creationPriority = -1)
public class RequestContext {

	// //////////////////////
	// VARIABLES
	// //////////////

	private HttpRequest httpRequest;
	private HttpResponse httpResponse;
	private Map<String, String> urlResources;
	private boolean shouldWriteResponse;
	private Boolean shouldResolveOutput;

	@Wired private List<Flushable> flushables;
	@Wired private List<Cancelable> cancelables;

	@Wired(required = false) private IResponseWriter responseWriter;

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

	public void flush() {
		this.flushables.forEach(Flushable::flush);
	}

	public void cancel() {
		this.cancelables.forEach(Cancelable::cancel);
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

	public boolean isShouldWriteResponse() {
		return this.shouldWriteResponse;
	}

	public Boolean shouldResolveOutput() {
		return this.shouldResolveOutput;
	}

	public void setShouldResolveOutput(Boolean shouldResolveOutput) {
		this.shouldResolveOutput = shouldResolveOutput;
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

	public String getStringParameter(String key) {
		HttpRequest httpRequest = this.getHttpRequest();
		String[] values = httpRequest.getQueryParameters().get(key);
		String value = null;

		if (values != null && values.length > 0) {
			value = values[0];
		} else {
			if (httpRequest.getBodyParameters() != null) {
				List<FileItem> bodyValues = httpRequest.getBodyParameters().get(key);
				if (bodyValues != null && bodyValues.size() > 0) {
					FileItem bodyValue = bodyValues.get(0);
					if (bodyValue.isFormField()) {
						value = bodyValue.getString();
					} else {
						throw new BadParameterException(key);
					}
				}
			}
		}

		return value;
	}

}
