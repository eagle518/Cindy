/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller.local
// LocalHttpRequest.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 3:33:17 PM
////////

package co.mindie.cindy.webservice.controller.local;

import co.mindie.cindy.webservice.controller.HttpMethod;
import co.mindie.cindy.webservice.controller.manager.BodyFileItem;
import co.mindie.cindy.webservice.controller.manager.HttpRequest;
import co.mindie.cindy.core.tools.io.InputStreamCreator;
import me.corsin.javatools.array.ArrayUtils;
import org.apache.commons.fileupload.FileItem;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalHttpRequest implements HttpRequest {

	////////////////////////
	// VARIABLES
	////////////////

	private Map<String, String[]> queryParameters;
	private Map<String, List<FileItem>> bodyParameters;
	private Map<String, String> headers;
	private String pathInfo;
	private String serverUrl;
	private String remoteAddr;
	private HttpMethod method;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public LocalHttpRequest() {
		this.queryParameters = new HashMap<>();
		this.bodyParameters = new HashMap<>();

	}

	////////////////////////
	// METHODS
	////////////////

	public void putQueryParameter(String parameter, Object value) {
		String[] currentValues = this.queryParameters.get(parameter);

		if (currentValues == null) {
			currentValues = new String[] { value.toString() };
		} else {
			currentValues = ArrayUtils.addItem(currentValues, value.toString());
		}

		this.queryParameters.put(parameter, currentValues);
	}

	public void putBodyParameter(String parameter, String contentType, InputStreamCreator inputStreamCreator) {
		List<FileItem> fileItems = this.bodyParameters.get(parameter);

		if (fileItems == null) {
			fileItems = new ArrayList<>();
			this.bodyParameters.put(parameter, fileItems);
		}

		fileItems.add(new BodyFileItem(parameter, contentType, true, inputStreamCreator));
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public Map<String, String[]> getQueryParameters() {
		return this.queryParameters;
	}

	@Override
	public Map<String, List<FileItem>> getBodyParameters() {
		return this.bodyParameters;
	}

	@Override
	public String getPathInfo() {
		return this.pathInfo;
	}

	@Override
	public String getServerUrl() {
		return this.serverUrl;
	}

	@Override
	public String getRemoteAddr() {
		return this.remoteAddr;
	}

	@Override
	public HttpMethod getMethod() {
		return this.method;
	}

	public void setQueryParameters(Map<String, String[]> queryParameters) {
		this.queryParameters = queryParameters;
	}

	public void setBodyParameters(Map<String, List<FileItem>> bodyParameters) {
		this.bodyParameters = bodyParameters;
	}

	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	@Override
	public InputStream getBody() {
		return null;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void setHeader(String header, String value) {
		this.headers.put(header, value);
	}

	@Override
	public String getHeader(String headerKey) {
		return this.headers.get(headerKey);
	}

}
