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
import java.util.List;

import co.mindie.cindy.automapping.Box;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.automapping.WiredCore;
import co.mindie.cindy.component.ComponentAspect;
import co.mindie.cindy.component.box.ComponentBox;
import co.mindie.cindy.context.RequestContext;
import co.mindie.cindy.resolver.IResolver;

@Box(rejectAspects = ComponentAspect.SINGLETON)
public class RequestHandler<T> implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////

	@WiredCore private ComponentBox componentBox;

	@Wired
	private RequestContext requestContext;

	private T controller;

	private EndpointEntry endpointEntry;

	private List<IResolver> parametersResolver;
	private IResolver outputResolver;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public RequestHandler() {
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void close() {
		this.componentBox.close();
	}

	public void reset() {
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

	public T getController() {
		return controller;
	}

	public EndpointEntry getEndpointEntry() {
		return endpointEntry;
	}

	public void setEndpointEntry(EndpointEntry endpointEntry) {
		this.endpointEntry = endpointEntry;
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

	public IResolver getOutputResolver() {
		return outputResolver;
	}

	public void setOutputResolver(IResolver outputResolver) {
		this.outputResolver = outputResolver;
	}

	public List<IResolver> getParametersResolver() {
		return parametersResolver;
	}

	public void setParametersResolver(List<IResolver> parametersResolver) {
		this.parametersResolver = parametersResolver;
	}
}
