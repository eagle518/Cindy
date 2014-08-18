/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.modelconverter
// IModelConverterOutput.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 28, 2014 at 4:58:07 PM
////////

package co.mindie.cindy.modelconverter;

import co.mindie.cindy.component.ComponentContext;

public interface IResolverOutput {

	/**
	 * Create the required resolvers in the ComponentContext to resolve the inputObject.
	 * @param cc
	 * @param inputObject
	 * @param options
	 * @return
	 */
	Object createResolversAndResolve(ComponentContext cc, Object inputObject, int options);

}
