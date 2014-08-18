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
import org.joda.time.DateTime;

@Resolver(managedInputClasses = String.class, managedOutputClasses = DateTime[].class)
public class StringToDateTimeArrayResolver implements IResolver<String, DateTime[]> {

	////////////////////////
	// VARIABLES
	////////////////

	@Wired private StringToStringArrayResolver stringArrayResolver;
	@Wired private StringToDateTimeResolver dateTimeResolver;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	@Override
	public DateTime[] resolve(String input, Class<?> expectedOutputType, int options) {
		if (input == null) {
			return null;
		}

		String[] array = this.stringArrayResolver.resolve(input, String[].class, options);
		DateTime[] dateTimeArray = new DateTime[array.length];

		for (int i = 0; i < dateTimeArray.length; i++) {
			dateTimeArray[i] = this.dateTimeResolver.resolve(array[i], DateTime.class, options);
		}

		return dateTimeArray;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
