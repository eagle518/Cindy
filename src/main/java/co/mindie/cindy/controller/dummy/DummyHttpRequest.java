/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller.dummy
// DummyHttpRequest.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 3:33:17 PM
////////

package co.mindie.cindy.controller.dummy;

import co.mindie.cindy.automapping.HttpMethod;
import co.mindie.cindy.controller.manager.HttpRequest;
import org.apache.commons.fileupload.FileItem;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class DummyHttpRequest implements HttpRequest {

	////////////////////////
	// VARIABLES
	////////////////

	private Map<String, String[]> queryParameters;
	private Map<String, List<FileItem>> bodyParameters;
	private String pathInfo;
	private String serverUrl;
	private String remoteAddr;
	private HttpMethod method;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public DummyHttpRequest() {

	}

	////////////////////////
	// METHODS
	////////////////

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

}
