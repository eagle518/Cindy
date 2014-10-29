/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.serveradapter
// Parameter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 11:50:51 AM
////////

package co.mindie.cindy.controller.manager;

import java.io.InputStream;

public class RequestParameter {

	////////////////////////
	// VARIABLES
	////////////////

	final private String name;
	final private int resolverOptions;
	final private boolean required;
	private String stringValue;
	private InputStream inputStream;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public RequestParameter(String name, int resolverOptions, boolean required) {
		this.name = name;
		this.resolverOptions = resolverOptions;
		this.required = required;
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public String getName() {
		return this.name;
	}

	public String getStringValue() {
		return this.stringValue;
	}

	public InputStream getInputStream() {
		return this.inputStream;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public int getResolverOptions() {
		return resolverOptions;
	}

	public boolean isRequired() {
		return required;
	}
}
