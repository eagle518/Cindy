/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.modelconverter
// ChainedResolverOutput.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 4:09:16 PM
////////

package co.mindie.cindy.modelconverter;

import java.util.List;

import co.mindie.cindy.component.ComponentContext;

public class ChainedResolverOutput implements IResolverOutput {

	////////////////////////
	// VARIABLES
	////////////////

	private List<IResolverOutput> outputs;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ChainedResolverOutput(List<IResolverOutput> outputs) {
		this.outputs = outputs;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public Object createResolversAndResolve(ComponentContext cc, Object inputObject, int options) {
		for (IResolverOutput output : this.outputs) {
			inputObject = output.createResolversAndResolve(cc, inputObject, options);
		}

		return inputObject;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}