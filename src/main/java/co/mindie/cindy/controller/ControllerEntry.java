/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// ControllerEntry.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2014 at 10:36:01 AM
////////

package co.mindie.cindy.controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import co.mindie.cindy.CindyApp;
import co.mindie.cindy.component.ComponentContext;
import co.mindie.cindy.context.RequestContext;

public class ControllerEntry {

	////////////////////////
	// VARIABLES
	////////////////

	private Class<?> controllerClass;
	private String basePath;
	private CindyApp application;
	private boolean poolEnabled;
	private Deque<RequestHandler> pool;
	private List<EndpointEntry> endpoints;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ControllerEntry(Class<?> controllerClass, String basePath, boolean poolEnabled, CindyApp application) {
		this.controllerClass = controllerClass;
		this.basePath = basePath;
		this.application = application;
		this.poolEnabled = poolEnabled;
		this.pool = new ArrayDeque<>();
		this.endpoints = new ArrayList<>();
	}

	////////////////////////
	// METHODS
	////////////////

	public RequestHandler createRequestHandler(CindyApp application) {
		RequestHandler requestHandler = null;

		if (this.poolEnabled) {
			synchronized (this.pool) {
				if (!this.pool.isEmpty()) {
					requestHandler = this.pool.poll();
				}
			}
		}

		if (requestHandler == null) {
			requestHandler = new RequestHandler(this);

			ComponentContext cc = new ComponentContext(application.getComponentContext());
			requestHandler.setComponentContext(cc);
			requestHandler.setController(application.createComponent(cc, this.controllerClass));
			requestHandler.setRequestContext(application.findOrCreateComponent(cc, RequestContext.class));
		}

		return requestHandler;
	}

	public void releaseRequestHandler(RequestHandler requestHandler) {
		if (this.poolEnabled) {
			requestHandler.reset();

			synchronized (this.pool) {
				this.pool.add(requestHandler);
			}
		}
	}

	public void addEndpoint(EndpointEntry endpoint) {
		this.endpoints.add(endpoint);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Class<?> getControllerClass() {
		return this.controllerClass;
	}

	public String getBasePath() {
		return this.basePath;
	}

	public CindyApp getApplication() {
		return this.application;
	}

	public boolean isPoolEnabled() {
		return this.poolEnabled;
	}

	public List<EndpointEntry> getEndpoints() {
		return this.endpoints;
	}
}
