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
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.resolver.IResolver;

@Resolver(managedInputClasses = { String.class }, managedOutputClasses = { long[].class })
public class StringToLongArrayResolver implements IResolver<String, long[]> {

	////////////////////////
	// VARIABLES
	////////////////

	@Wired
	private StringToStringArrayResolver stringArrayResolver;
	@Wired private StringToLongResolver longResolver;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToLongArrayResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public long[] resolve(String input, Class<?> expectedOutputType, int options) {
		if (input == null) {
			return null;
		}

		String[] array = this.stringArrayResolver.resolve(input, String[].class, options);
		long[] longArray = new long[array.length];

		for (int i = 0; i < longArray.length; i++) {
			longArray[i] = this.longResolver.resolve(array[i], long.class, options);
		}

		return longArray;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
