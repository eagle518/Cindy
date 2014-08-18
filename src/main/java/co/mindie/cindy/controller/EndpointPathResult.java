/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// EndpointPathResult.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 24, 2014 at 12:48:51 PM
////////

package co.mindie.cindy.controller;

import java.util.ArrayList;
import java.util.List;

public class EndpointPathResult {

	////////////////////////
	// VARIABLES
	////////////////

	final private List<String> pathValues;
	private EndpointEntry endpointEntry;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public EndpointPathResult() {
		this.pathValues = new ArrayList<String>();
	}

	////////////////////////
	// METHODS
	////////////////

	public void addPathValue(String pathValue) {
		this.pathValues.add(pathValue);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public EndpointEntry getEndpointEntry() {
		return this.endpointEntry;
	}

	public void setEndpointEntry(EndpointEntry endpointEntry) {
		this.endpointEntry = endpointEntry;
	}

	public List<String> getPathValues() {
		return this.pathValues;
	}
}
