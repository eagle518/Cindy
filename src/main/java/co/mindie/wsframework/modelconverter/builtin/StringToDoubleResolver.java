/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.modelconverter.impl
// StringToIntConverter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 24, 2014 at 3:12:27 PM
////////

package co.mindie.wsframework.modelconverter.builtin;

import me.corsin.javatools.misc.NullArgumentException;
import co.mindie.wsframework.automapping.Resolver;
import co.mindie.wsframework.modelconverter.IResolver;

@Resolver(managedInputClasses = { String.class }, managedOutputClasses = { double.class, Double.class })
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
