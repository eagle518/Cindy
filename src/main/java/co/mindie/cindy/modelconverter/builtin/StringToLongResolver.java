/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.modelconverter.impl
// StringToIntConverter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 24, 2014 at 3:12:27 PM
////////

package co.mindie.cindy.modelconverter.builtin;

import co.mindie.cindy.automapping.Resolver;
import co.mindie.cindy.modelconverter.IResolver;
import me.corsin.javatools.misc.NullArgumentException;

@Resolver(managedInputClasses = { String.class }, managedOutputClasses = { long.class, Long.class })
public class StringToLongResolver implements IResolver<String, Long> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToLongResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public Long resolve(String input, Class<?> expectedOutputType, int options) {
		if (expectedOutputType == null) {
			expectedOutputType = Long.class;
		}

		if (input == null) {
			if (expectedOutputType == Long.class) {
				return null;
			} else {
				throw new NullArgumentException("input");
			}
		}

		return Long.valueOf(input);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
