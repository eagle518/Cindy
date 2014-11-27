/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver.builtin
// ArrayToListResolver.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 6:19:14 PM
////////

package co.mindie.cindy.webservice.resolver.builtin;

import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.webservice.annotation.Resolver;
import co.mindie.cindy.webservice.resolver.IResolver;
import co.mindie.cindy.webservice.resolver.ResolverContext;

import java.util.ArrayList;
import java.util.List;

@Load(creationPriority = -1)
@Resolver(managedInputClasses = Object[].class, managedOutputClasses = List.class)
public class ArrayToListResolver implements IResolver<Object[], List<Object>> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ArrayToListResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public List<Object> resolve(Object[] input, Class<?> expectedOutputType, ResolverContext resolverContext) {
		if (input == null) {
			return null;
		}

		List<Object> output = new ArrayList<>();

		for (Object obj : input) {
			output.add(obj);
		}

		return output;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
