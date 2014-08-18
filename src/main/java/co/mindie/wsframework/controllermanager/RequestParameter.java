/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.serveradapter
// Parameter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 11:50:51 AM
////////

package co.mindie.wsframework.controllermanager;

import java.io.InputStream;

public class RequestParameter {

	////////////////////////
	// VARIABLES
	////////////////

	final private String name;
	private String stringValue;
	private InputStream inputStream;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public RequestParameter(String name) {
		this.name = name;
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
}
