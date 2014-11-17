/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// ControllerEntry.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2014 at 10:36:01 AM
////////

package co.mindie.cindy.controller.manager.entry;

import java.util.ArrayList;
import java.util.List;

public class ControllerEntry {

	////////////////////////
	// VARIABLES
	////////////////

	private Class<?> controllerClass;
	private String basePath;
	private List<EndpointEntry> endpoints;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ControllerEntry(Class<?> controllerClass, String basePath) {
		this.controllerClass = controllerClass;
		this.basePath = basePath;
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

	public List<EndpointEntry> getEndpoints() {
		return this.endpoints;
	}
}
