/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver
// ChainedResolverBuilder.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 4:09:16 PM
////////

package co.mindie.cindy.resolver;

import java.util.ArrayList;
import java.util.List;

import co.mindie.cindy.component.ComponentInitializer;
import co.mindie.cindy.component.box.ComponentBox;

public class ChainedResolverBuilder implements IResolverBuilder {

	////////////////////////
	// VARIABLES
	////////////////

	private List<IResolverBuilder> outputs;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ChainedResolverBuilder(List<IResolverBuilder> outputs) {
		this.outputs = outputs;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public IResolver findOrCreateResolver(ComponentInitializer initializer, ComponentBox box) {
		final List<IResolver> resolvers = new ArrayList<>();

		for (IResolverBuilder builder : this.outputs) {
			resolvers.add(builder.findOrCreateResolver(initializer, box));
		}

		return new IResolver() {
			@Override
			public Object resolve(Object o, Class expectedOutputType, int options) {
				Object output = o;

				for (IResolver resolver : resolvers) {
					output = resolver.resolve(output, expectedOutputType, options);
				}

				return output;
			}
		};
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
