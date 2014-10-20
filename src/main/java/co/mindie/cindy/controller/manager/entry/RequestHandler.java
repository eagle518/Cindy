/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// RequestHandler.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 23, 2014 at 7:22:39 PM
////////

package co.mindie.cindy.controller.manager.entry;

import java.io.Closeable;

import co.mindie.cindy.component.ComponentBox;
import co.mindie.cindy.context.RequestContext;
import co.mindie.cindy.controller.CindyController;

public class RequestHandler implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////

	private Object controller;
	private RequestContext requestContext;
	private ComponentBox componentBox;
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
		this.componentBox.close();
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

	public ComponentBox getComponentBox() {
		return this.componentBox;
	}

	public void setComponentBox(ComponentBox componentBox) {
		this.componentBox = componentBox;
	}

}
