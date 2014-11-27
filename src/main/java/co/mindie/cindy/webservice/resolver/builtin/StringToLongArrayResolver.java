/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver.impl
// StringToIntConverter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 24, 2014 at 3:12:27 PM
////////

package co.mindie.cindy.webservice.resolver.builtin;

import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.webservice.annotation.Resolver;
import co.mindie.cindy.core.annotation.Wired;
import co.mindie.cindy.webservice.resolver.IResolver;
import co.mindie.cindy.webservice.resolver.ResolverContext;

@Load(creationPriority = -1)
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
	public long[] resolve(String input, Class<?> expectedOutputType, ResolverContext resolverContext) {
		if (input == null) {
			return null;
		}

		String[] array = this.stringArrayResolver.resolve(input, String[].class, resolverContext);
		long[] longArray = new long[array.length];

		for (int i = 0; i < longArray.length; i++) {
			longArray[i] = this.longResolver.resolve(array[i], long.class, resolverContext);
		}

		return longArray;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
