/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver
// IModelConverterOutput.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 28, 2014 at 4:58:07 PM
////////

package co.mindie.cindy.resolver;

import co.mindie.cindy.component.box.ComponentBox;

public interface IResolverOutput {

	/**
	 * Create the required resolvers in the ComponentBox to resolve the inputObject.
	 * @param cc
	 * @param inputObject
	 * @param options
	 * @return
	 */
	Object createResolversAndResolve(ComponentBox cc, Object inputObject, int options);

}
