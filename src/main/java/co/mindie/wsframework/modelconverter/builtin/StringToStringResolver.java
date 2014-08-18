/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.modelconverter.impl
// StringToIntConverter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 24, 2014 at 3:12:27 PM
////////

package co.mindie.wsframework.modelconverter.builtin;

import co.mindie.wsframework.automapping.Resolver;
import co.mindie.wsframework.modelconverter.IResolver;

@Resolver(managedInputClasses = { String.class }, managedOutputClasses = { String.class })
public class StringToStringResolver implements IResolver<String, String> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToStringResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public String resolve(String input, Class<?> expectedOutputType, int options) {
		return input;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
