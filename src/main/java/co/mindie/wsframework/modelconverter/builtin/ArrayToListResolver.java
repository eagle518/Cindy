/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.modelconverter.builtin
// ArrayToListResolver.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 6:19:14 PM
////////

package co.mindie.wsframework.modelconverter.builtin;

import java.util.ArrayList;
import java.util.List;

import co.mindie.wsframework.automapping.Resolver;
import co.mindie.wsframework.modelconverter.IResolver;

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
	public List<Object> resolve(Object[] input, Class<?> expectedOutputType, int options) {
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
