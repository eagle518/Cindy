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

@Resolver(managedInputClasses = { String.class }, managedOutputClasses = { float[].class })
public class StringToFloatArrayResolver implements IResolver<String, float[]> {

	////////////////////////
	// VARIABLES
	////////////////

	@Wired
	private StringToStringArrayResolver stringArrayResolver;
	@Wired private StringToFloatResolver floatResolver;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToFloatArrayResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public float[] resolve(String input, Class<?> expectedOutputType, int options) {
		if (input == null) {
			return null;
		}

		String[] array = this.stringArrayResolver.resolve(input, String[].class, options);
		float[] floatArray = new float[array.length];

		for (int i = 0; i < floatArray.length; i++) {
			floatArray[i] = this.floatResolver.resolve(array[i], float.class, options);
		}

		return floatArray;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
