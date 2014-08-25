/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// RequestHandler.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 23, 2014 at 7:22:39 PM
////////

package co.mindie.cindy.controller;

import java.io.Closeable;

import co.mindie.cindy.component.ComponentContext;
import co.mindie.cindy.context.RequestContext;

public class RequestHandler implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////

	private Object controller;
	private RequestContext requestContext;
	private ComponentContext componentContext;
	private EndpointEntry endpointEntry;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public RequestHandler(EndpointEntry endpointEntry) {
		this.endpointEntry = endpointEntry;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void close() {
		this.componentContext.close();
	}

	public void reset() {
		if (this.controller instanceof CindyController) {
			((CindyController)this.controller).reset();
		}
		this.requestContext.reset();
	}

	public void release(boolean useReusePool) {
		this.close();
		if (this.endpointEntry != null) {
			this.endpointEntry.releaseRequestHandler(this, useReusePool);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Object getController() {
		return this.controller;
	}

	public void setController(Object controller) {
		this.controller = controller;
	}

	public RequestContext getRequestContext() {
		return this.requestContext;
	}

	public void setRequestContext(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	public ComponentContext getComponentContext() {
		return this.componentContext;
	}

	public void setComponentContext(ComponentContext componentContext) {
		this.componentContext = componentContext;
	}

}
