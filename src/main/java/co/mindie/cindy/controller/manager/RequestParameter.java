/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.serveradapter
// Parameter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 11:50:51 AM
////////

package co.mindie.cindy.controller.manager;

import co.mindie.cindy.controller.manager.entry.RequestParameterResolverOption;

import java.util.List;

public class RequestParameter {

	////////////////////////
	// VARIABLES
	////////////////

	final private String name;
	final private boolean required;
	final private List<RequestParameterResolverOption> resolverOptions;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public RequestParameter(String name, boolean required, List<RequestParameterResolverOption> resolverOptions) {
		this.name = name;
		this.required = required;
		this.resolverOptions = resolverOptions;
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public String getName() {
		return name;
	}

	public List<RequestParameterResolverOption> getResolverOptions() {
		return resolverOptions;
	}

	public boolean isRequired() {
		return required;
	}
}
