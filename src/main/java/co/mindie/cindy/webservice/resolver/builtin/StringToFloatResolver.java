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
import co.mindie.cindy.webservice.resolver.IResolver;
import co.mindie.cindy.webservice.resolver.ResolverContext;
import me.corsin.javatools.misc.NullArgumentException;

@Load(creationPriority = -1)
@Resolver(managedInputClasses = {String.class}, managedOutputClasses = {Float.class, float.class})
public class StringToFloatResolver implements IResolver<String, Float> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToFloatResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public Float resolve(String input, Class<?> expectedOutputType, ResolverContext resolverContext) {
		if (expectedOutputType == null) {
			expectedOutputType = Float.class;
		}

		if (input == null) {
			if (expectedOutputType == Float.class) {
				return null;
			} else {
				throw new NullArgumentException("input");
			}
		}

		return Float.valueOf(input);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
