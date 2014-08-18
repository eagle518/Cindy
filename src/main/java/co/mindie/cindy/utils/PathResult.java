/////////////////////////////////////////////////
// Project : exiled-masterserver
// Package : com.kerious.exiled.masterserver.api.utils
// PathResult.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 11, 2013 at 1:54:51 PM
////////

package co.mindie.cindy.utils;

import java.util.HashMap;
import java.util.Map;

public class PathResult<T> {

	////////////////////////
	// VARIABLES
	////////////////

	private T element;
	private Map<String, String> parameters;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public PathResult() {
		this.parameters = new HashMap<String, String>();
	}

	public PathResult(T element) {
		this();

		this.element = element;
	}

	////////////////////////
	// METHODS
	////////////////

	public void addParameter(String parameterName, String parameterValue) {
		this.parameters.put(parameterName, parameterValue);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final T getElement() {
		return this.element;
	}

	public final void setElement(T element) {
		this.element = element;
	}

	public final Map<String, String> getParameters() {
		return this.parameters;
	}

}
