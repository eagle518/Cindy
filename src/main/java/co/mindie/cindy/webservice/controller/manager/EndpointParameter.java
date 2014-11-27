/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.serveradapter
// RequestParameter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 1, 2014 at 5:56:13 PM
////////

package co.mindie.cindy.webservice.controller.manager;

public class EndpointParameter {

	////////////////////////
	// VARIABLES
	////////////////

	private RequestParameters requestParameters;
	private String name;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public EndpointParameter() {

	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public RequestParameters getRequestParameters() {
		return this.requestParameters;
	}

	public String getName() {
		return this.name;
	}

	public void setRequestParameters(RequestParameters requestParameters) {
		this.requestParameters = requestParameters;
	}

	public void setName(String name) {
		this.name = name;
	}
}
