/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.modelconverter
// ModelConverterContext.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 6, 2014 at 1:53:33 PM
////////

package co.mindie.wsframework.modelconverter;

import co.mindie.wsframework.context.RequestContext;

import java.util.HashMap;
import java.util.Map;

public class ModelConverterContext {

	////////////////////////
	// VARIABLES
	////////////////

	private Map<Object, Object> cache;
	private RequestContext requestContext;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ModelConverterContext(RequestContext requestContext) {
		this.requestContext = requestContext;
		this.cache = new HashMap<>();
	}

	public ModelConverterContext() {
		this(null);
	}

	////////////////////////
	// METHODS
	////////////////

	public void save(Object input, Object output) {
		this.cache.put(input, output);
	}

	public Object restore(Object input) {
		return this.cache.get(input);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public RequestContext getRequestContext() {
		return this.requestContext;
	}

	public void setRequestContext(RequestContext requestContext) {
		this.requestContext = requestContext;
	}
}
