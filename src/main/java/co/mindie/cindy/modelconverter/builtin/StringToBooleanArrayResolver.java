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
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.modelconverter.IResolver;

@Resolver(managedInputClasses = { String.class }, managedOutputClasses = { boolean[].class })
public class StringToBooleanArrayResolver implements IResolver<String, boolean[]> {

	////////////////////////
	// VARIABLES
	////////////////

	@Wired
	private StringToStringArrayResolver stringArrayResolver;
	@Wired private StringToBooleanResolver booleanResolver;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToBooleanArrayResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public boolean[] resolve(String input, Class<?> expectedOutputType, int options) {
		if (input == null) {
			return null;
		}

		String[] array = this.stringArrayResolver.resolve(input, String[].class, options);
		boolean[] doubleArray = new boolean[array.length];

		for (int i = 0; i < doubleArray.length; i++) {
			doubleArray[i] = this.booleanResolver.resolve(array[i], boolean.class, options);
		}

		return doubleArray;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
