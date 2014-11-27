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
	public float[] resolve(String input, Class<?> expectedOutputType, ResolverContext resolverContext) {
		if (input == null) {
			return null;
		}

		String[] array = this.stringArrayResolver.resolve(input, String[].class, resolverContext);
		float[] floatArray = new float[array.length];

		for (int i = 0; i < floatArray.length; i++) {
			floatArray[i] = this.floatResolver.resolve(array[i], float.class, resolverContext);
		}

		return floatArray;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
