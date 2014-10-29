/////////////////////////////////////////////////
// Project : Mindie WebService
// Package : co.mindie.webservice.resolver.impl
// ContentModelConverter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Feb 13, 2014 at 1:46:18 PM
////////

package co.mindie.cindy.resolver.builtin;

import java.util.ArrayList;
import java.util.Collection;
import co.mindie.cindy.resolver.IResolver;

public class CollectionResolver<Input, Output> implements IResolver<Collection<Input>, Collection<Output>> {

	////////////////////////
	// VARIABLES
	////////////////

	final private IResolver<Input, Output> singleResolver;
	final private Class<Output> outputClass;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CollectionResolver(Class<Output> outputClass, IResolver<Input, Output> singleResolver) {
		this.singleResolver = singleResolver;
		this.outputClass = outputClass;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public Collection<Output> resolve(Collection<Input> inputs, Class<?> expectedOutputType, int options) {
		if (inputs == null) {
			return null;
		}

		Collection<Output> outputs = new ArrayList<>();

		for (Input input : inputs) {
			this.singleResolver.resolve(input, expectedOutputType, options);
		}

		return outputs;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}