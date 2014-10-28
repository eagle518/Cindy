/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver.builtin
// StringToDateTimeResolver.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 28, 2014 at 1:44:50 PM
////////

package co.mindie.cindy.resolver.builtin;

import co.mindie.cindy.automapping.Load;
import co.mindie.cindy.automapping.Resolver;
import org.joda.time.DateTime;

import co.mindie.cindy.resolver.IResolver;

@Load
@Resolver(managedInputClasses = String.class, managedOutputClasses = DateTime.class)
public class StringToDateTimeResolver implements IResolver<String, DateTime> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToDateTimeResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public DateTime resolve(String input, Class<?> expectedOutputType, int options) {
		if (input == null) {
			return null;
		}

		return DateTime.parse(input);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
