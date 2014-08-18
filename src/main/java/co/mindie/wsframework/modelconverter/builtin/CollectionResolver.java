/////////////////////////////////////////////////
// Project : Mindie WebService
// Package : co.mindie.webservice.modelconverter.impl
// ContentModelConverter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Feb 13, 2014 at 1:46:18 PM
////////

package co.mindie.wsframework.modelconverter.builtin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.mindie.wsframework.WSApplication;
import co.mindie.wsframework.automapping.Resolver;
import co.mindie.wsframework.automapping.Wired;
import co.mindie.wsframework.component.WSComponent;
import co.mindie.wsframework.exception.WSFrameworkException;
import co.mindie.wsframework.modelconverter.IResolver;
import co.mindie.wsframework.modelconverter.IResolverOutput;

@Resolver(managedInputClasses = { ArrayList.class, List.class, Collection.class }, managedOutputClasses = { ArrayList.class, List.class, Collection.class },
isDefaultForInputTypes = true)
@SuppressWarnings({ "rawtypes" })
public class CollectionResolver extends WSComponent implements IResolver<Collection, Collection> {

	// //////////////////////
	// VARIABLES
	// //////////////

	@Wired private WSApplication application;

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
					resolverOutput = this.application.getModelConverterManager().getDefaultResolverOutputForInput(obj);
					if (resolverOutput == null) {
						throw new WSFrameworkException("No Resolver found for input type " + obj.getClass());
					}
				}

				output.add(resolverOutput.createResolversAndResolve(this.getComponentContext(), obj, options));
			}
		}

		return output;
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////
}
