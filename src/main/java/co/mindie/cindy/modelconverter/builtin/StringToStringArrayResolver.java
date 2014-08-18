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
import me.corsin.javatools.string.Strings;

@Resolver(managedInputClasses = {String.class}, managedOutputClasses = {String[].class})
public class StringToStringArrayResolver implements IResolver<String, String[]> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToStringArrayResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public String[] resolve(String input, Class<?> expectedOutputType, int options) {
		if (input == null) {
			return null;
		}

		if (Strings.isNullOrEmpty(input)) {
			return new String[0];
		}

		// Mindie's arrays were historically using a separator=";"
		// So we try to split with the "right" separator (",") and if we have only 1 result
		// we try with the ";"
		// That's pretty dirty but... let's try.

		String[] array = input.split(",");
		if (array.length != 1) {
			return array;
		} else {
			return input.split(";");
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
