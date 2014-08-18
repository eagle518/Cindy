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

@Resolver(managedInputClasses = { String.class }, managedOutputClasses = { boolean.class, Boolean.class })
public class StringToBooleanResolver implements IResolver<String, Boolean> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToBooleanResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public Boolean resolve(String input, Class<?> expectedOutputType, int options) {
		if (expectedOutputType == null) {
			expectedOutputType = Boolean.class;
		}

		if (input == null) {
			if (expectedOutputType == Boolean.class) {
				return null;
			} else {
				throw new NullArgumentException("input");
			}
		}

		return Boolean.valueOf(input);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
