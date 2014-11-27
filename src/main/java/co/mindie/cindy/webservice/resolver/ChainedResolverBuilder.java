/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver
// ChainedResolverBuilder.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 4:09:16 PM
////////

package co.mindie.cindy.webservice.resolver;

import co.mindie.cindy.core.component.initializer.ComponentInitializer;
import co.mindie.cindy.core.component.box.ComponentBox;

import java.util.ArrayList;
import java.util.List;

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
			public Object resolve(Object o, Class expectedOutputType, ResolverContext resolverContext) {
				Object output = o;

				for (IResolver resolver : resolvers) {
					output = resolver.resolve(output, expectedOutputType, resolverContext);
				}

				return output;
			}
		};
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
