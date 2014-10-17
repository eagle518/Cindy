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

@Resolver(managedInputClasses = { String.class }, managedOutputClasses = { Double.class, double.class })
public class StringToDoubleResolver implements IResolver<String, Double> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToDoubleResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public Double resolve(String input, Class<?> expectedOutputType, int options) {
		if (expectedOutputType == null) {
			expectedOutputType = Double.class;
		}

		if (input == null) {
			if (expectedOutputType == Double.class) {
				return null;
			} else {
				throw new NullArgumentException("input");
			}
		}

		return Double.valueOf(input);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
