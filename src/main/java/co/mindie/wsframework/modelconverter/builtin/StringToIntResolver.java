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

@Resolver(managedInputClasses = { String.class }, managedOutputClasses = { int.class, Integer.class })
public class StringToIntResolver implements IResolver<String, Integer> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToIntResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public Integer resolve(String input, Class<?> expectedOutputType, int options) {
		if (expectedOutputType == null) {
			expectedOutputType = Integer.class;
		}

		if (input == null) {
			if (expectedOutputType == Integer.class) {
				return null;
			} else {
				throw new NullArgumentException("input");
			}
		}

		return Integer.valueOf(input);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
