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
	public int[] resolve(String input, Class<?> expectedOutputType, ResolverContext resolverContext) {
		if (input == null) {
			return null;
		}

		String[] array = this.stringArrayResolver.resolve(input, String[].class, resolverContext);
		int[] intArray = new int[array.length];

		for (int i = 0; i < intArray.length; i++) {
			intArray[i] = this.intResolver.resolve(array[i], int.class, resolverContext);
		}

		return intArray;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
