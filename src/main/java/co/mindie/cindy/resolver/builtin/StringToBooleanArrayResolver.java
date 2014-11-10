/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver.impl
// StringToIntConverter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 24, 2014 at 3:12:27 PM
////////

package co.mindie.cindy.resolver.builtin;

import co.mindie.cindy.automapping.Load;
import co.mindie.cindy.automapping.Resolver;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.resolver.IResolver;
import co.mindie.cindy.resolver.ResolverContext;

@Load(creationPriority = -1)
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
	public boolean[] resolve(String input, Class<?> expectedOutputType, ResolverContext resolverContext) {
		if (input == null) {
			return null;
		}

		String[] array = this.stringArrayResolver.resolve(input, String[].class, resolverContext);
		boolean[] doubleArray = new boolean[array.length];

		for (int i = 0; i < doubleArray.length; i++) {
			doubleArray[i] = this.booleanResolver.resolve(array[i], boolean.class, resolverContext);
		}

		return doubleArray;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
