/////////////////////////////////////////////////
// Project : webservice
// Package : com.ever.webservice.context
// RequestContext.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 4, 2013 at 3:45:44 PM
////////

package co.mindie.cindy.webservice.context;

import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.core.annotation.Wired;
import co.mindie.cindy.core.component.CreationBox;
import co.mindie.cindy.core.tools.Cancelable;
import co.mindie.cindy.core.tools.Flushable;
import co.mindie.cindy.webservice.controller.ParamSource;
import co.mindie.cindy.webservice.controller.manager.HttpRequest;
import co.mindie.cindy.webservice.controller.manager.HttpResponse;
import co.mindie.cindy.webservice.exception.ResolverException;
import co.mindie.cindy.webservice.responsewriter.IResponseWriter;
import co.mindie.cindy.webservice.stats.WebserviceStats;
import org.apache.commons.fileupload.FileItem;

import java.io.UnsupportedEncodingException;
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

	@Wired(required = false, creationBox = CreationBox.NO_CREATION) private WebserviceStats stats;
	@Wired(required = false) private IResponseWriter responseWriter;
	private String endpointMethodName;

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
		if (this.stats != null) {
			this.stats.notifyRequestStarted();
		}
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
		if (this.stats != null) {
			this.stats.notifyRequestEnded();
		}
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

	public String getStringParameter(String parameterName) {
		return this.getStringParameter(parameterName, ParamSource.AUTO);
	}

	public String getStringParameter(String parameterName, ParamSource source) {
		String stringValue = null;

		if (source == ParamSource.AUTO || source == ParamSource.URL) {
			stringValue = this.getUrlResources().get(parameterName);
		}

		if (source == ParamSource.QUERY || (stringValue == null && source == ParamSource.AUTO)) {
			String[] queryParameters = this.getHttpRequest().getQueryParameters().get(parameterName);
			if (queryParameters != null && queryParameters.length > 0) {
				stringValue = queryParameters[0];
			}
		}

		if (source == ParamSource.BODY || (stringValue == null && source == ParamSource.AUTO)) {
			if (this.getHttpRequest().getBodyParameters() != null) {
				List<FileItem> items = this.getHttpRequest().getBodyParameters().get(parameterName);

				if (items != null && items.size() > 0) {
					FileItem item = items.get(0);
					if (item.isFormField()) {
						try {
							stringValue = item.getString("UTF-8");
						} catch (UnsupportedEncodingException e) {
							throw new ResolverException("Unable to get parameterName " + parameterName, e);
						}
					}
				}
			}
		}

		if (source == ParamSource.HEADER || (stringValue == null && source == ParamSource.AUTO)) {
			stringValue = this.getHttpRequest().getHeader(parameterName);
		}

		return stringValue;
	}

	public void setEndpointMethodName(String endpointMethodName) {
		this.endpointMethodName = endpointMethodName;
	}

	public String getEndpointMethodName() {
		return endpointMethodName;
	}
}
