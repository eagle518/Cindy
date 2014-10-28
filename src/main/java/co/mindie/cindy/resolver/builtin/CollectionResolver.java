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
import java.util.List;

import co.mindie.cindy.CindyWebApp;
import co.mindie.cindy.automapping.Load;
import co.mindie.cindy.automapping.Resolver;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.component.ComponentBoxListenerImpl;
import co.mindie.cindy.exception.CindyException;
import co.mindie.cindy.resolver.IResolver;
import co.mindie.cindy.resolver.IResolverOutput;

@Load
@Resolver(managedInputClasses = { ArrayList.class, List.class, Collection.class }, managedOutputClasses = { ArrayList.class, List.class, Collection.class },
isDefaultForInputTypes = true)
@SuppressWarnings({ "rawtypes" })
public class CollectionResolver extends ComponentBoxListenerImpl implements IResolver<Collection, Collection> {

	// //////////////////////
	// VARIABLES
	// //////////////

	@Wired
	private CindyWebApp application;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	// //////////////////////
	// METHODS
	// //////////////

	@Override
	public Collection resolve(Collection input, Class<?> expectedOutputType, int options) {
		if (input == null) {
			return null;
		}

		Collection<Object> output = new ArrayList<>(input.size());

		IResolverOutput resolverOutput = null;

		for (Object obj : input) {
			if (obj == null) {
				output.add(null);
			} else {
				if (resolverOutput == null) {
					resolverOutput = this.application.getResolverManager().getDefaultResolverOutputForInput(obj);
					if (resolverOutput == null) {
						throw new CindyException("No Resolver found for input type " + obj.getClass());
					}
				}

				output.add(resolverOutput.createResolversAndResolve(this.getEnclosingBox(), obj, options));
			}
		}

		return output;
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////
}
