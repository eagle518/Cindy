/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// RequestHandler.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 23, 2014 at 7:22:39 PM
////////

package co.mindie.cindy.webservice.controller.manager.entry;

import co.mindie.cindy.core.annotation.Box;
import co.mindie.cindy.core.annotation.Wired;
import co.mindie.cindy.core.annotation.Core;
import co.mindie.cindy.core.component.Aspect;
import co.mindie.cindy.core.component.box.ComponentBox;
import co.mindie.cindy.webservice.authentificator.RequestContextAuthenticator;
import co.mindie.cindy.webservice.context.RequestContext;
import co.mindie.cindy.webservice.resolver.IResolver;
import co.mindie.cindy.webservice.resolver.ResolverContext;

import java.io.Closeable;
import java.util.List;

@Box(rejectAspects = Aspect.SINGLETON)
public class RequestHandler implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////

	@Core
	private ComponentBox componentBox;

	@Wired
	private RequestContext requestContext;

	@Wired(required = false)
	private RequestContextAuthenticator requestContextAuthenticator;

	private Object controller;

	private EndpointEntry endpointEntry;

	private List<IResolver> parametersResolver;
	private List<ResolverContext> parametersResolverContexts;

	private IResolver outputResolver;
	private ResolverContext outputResolverContext;

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
		if (this.outputResolverContext != null) {
			this.outputResolverContext.cancelBatchOperations();
		}
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

	public Object getController() {
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

	public List<ResolverContext> getParametersResolverContexts() {
		return parametersResolverContexts;
	}

	public void setParametersResolverContexts(List<ResolverContext> parametersResolverContexts) {
		this.parametersResolverContexts = parametersResolverContexts;
	}

	public ResolverContext getOutputResolverContext() {
		return outputResolverContext;
	}

	public void setOutputResolverContext(ResolverContext outputResolverContext) {
		this.outputResolverContext = outputResolverContext;
	}

	public RequestContextAuthenticator getRequestContextAuthenticator() {
		return requestContextAuthenticator;
	}
}
