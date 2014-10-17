/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver.impl
// StringToIntConverter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 24, 2014 at 3:12:27 PM
////////

package co.mindie.cindy.resolver.builtin;

import co.mindie.cindy.automapping.Resolver;
import co.mindie.cindy.resolver.IResolver;
import me.corsin.javatools.misc.NullArgumentException;

@Resolver(managedInputClasses = {String.class}, managedOutputClasses = {Float.class, float.class})
public class StringToFloatResolver implements IResolver<String, Float> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToFloatResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public Float resolve(String input, Class<?> expectedOutputType, int options) {
		if (expectedOutputType == null) {
			expectedOutputType = Float.class;
		}

		if (input == null) {
			if (expectedOutputType == Float.class) {
				return null;
			} else {
				throw new NullArgumentException("input");
			}
		}

		return Float.valueOf(input);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
