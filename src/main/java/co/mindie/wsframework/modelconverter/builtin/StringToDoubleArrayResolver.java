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

@Resolver(managedInputClasses = { String.class }, managedOutputClasses = { double[].class })
public class StringToDoubleArrayResolver implements IResolver<String, double[]> {

	////////////////////////
	// VARIABLES
	////////////////

	@Wired private StringToStringArrayResolver stringArrayResolver;
	@Wired private StringToDoubleResolver doubleResolver;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToDoubleArrayResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public double[] resolve(String input, Class<?> expectedOutputType, int options) {
		if (input == null) {
			return null;
		}

		String[] array = this.stringArrayResolver.resolve(input, String[].class, options);
		double[] doubleArray = new double[array.length];

		for (int i = 0; i < doubleArray.length; i++) {
			doubleArray[i] = this.doubleResolver.resolve(array[i], double.class, options);
		}

		return doubleArray;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
