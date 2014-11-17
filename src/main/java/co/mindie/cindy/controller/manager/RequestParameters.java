/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.serveradapter
// RequestParameters.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 1, 2014 at 5:04:37 PM
////////

package co.mindie.cindy.controller.manager;

import org.apache.commons.fileupload.FileItem;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class RequestParameters {

	////////////////////////
	// VARIABLES
	////////////////

	private Map<String, String> queryParameters;
	private Map<String, List<FileItem>> bodyParameters;
	private InputStream body;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public RequestParameters() {

	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Map<String, String> getQueryParameters() {
		return this.queryParameters;
	}

	public Map<String, List<FileItem>> getBodyParameters() {
		return this.bodyParameters;
	}

	public InputStream getBody() {
		return this.body;
	}

	public void setQueryParameters(Map<String, String> queryParameters) {
		this.queryParameters = queryParameters;
	}

	public void setBodyParameters(Map<String, List<FileItem>> bodyParameters) {
		this.bodyParameters = bodyParameters;
	}

	public void setBody(InputStream body) {
		this.body = body;
	}
}
