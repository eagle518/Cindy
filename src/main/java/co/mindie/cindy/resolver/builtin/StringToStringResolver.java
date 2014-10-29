/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver.impl
// StringToIntConverter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 24, 2014 at 3:12:27 PM
////////

package co.mindie.cindy.resolver.builtin;

import co.mindie.cindy.automapping.Load;
import co.mindie.cindy.automapping.Resolver;
import co.mindie.cindy.resolver.IResolver;

@Load(creationPriority = -1)
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
