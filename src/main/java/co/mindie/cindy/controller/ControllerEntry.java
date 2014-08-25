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
	private List<EndpointEntry> endpoints;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ControllerEntry(Class<?> controllerClass, String basePath, CindyApp application) {
		this.controllerClass = controllerClass;
		this.basePath = basePath;
		this.application = application;
		this.endpoints = new ArrayList<>();
	}

	////////////////////////
	// METHODS
	////////////////

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

	public List<EndpointEntry> getEndpoints() {
		return this.endpoints;
	}
}
