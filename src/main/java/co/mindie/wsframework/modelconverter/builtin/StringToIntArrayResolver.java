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
import co.mindie.wsframework.automapping.Wired;
import co.mindie.wsframework.modelconverter.IResolver;

@Resolver(managedInputClasses = { String.class }, managedOutputClasses = { int[].class })
public class StringToIntArrayResolver implements IResolver<String, int[]> {

	////////////////////////
	// VARIABLES
	////////////////

	@Wired private StringToStringArrayResolver stringArrayResolver;
	@Wired private StringToIntResolver intResolver;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToIntArrayResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public int[] resolve(String input, Class<?> expectedOutputType, int options) {
		if (input == null) {
			return null;
		}

		String[] array = this.stringArrayResolver.resolve(input, String[].class, options);
		int[] intArray = new int[array.length];

		for (int i = 0; i < intArray.length; i++) {
			intArray[i] = this.intResolver.resolve(array[i], int.class, options);
		}

		return intArray;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
