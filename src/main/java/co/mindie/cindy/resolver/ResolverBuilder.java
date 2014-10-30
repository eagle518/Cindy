/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver
// ModelConverterOutput.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 20, 2014 at 5:24:53 PM
////////

package co.mindie.cindy.resolver;

import co.mindie.cindy.component.ComponentInitializer;
import co.mindie.cindy.component.box.ComponentBox;
import co.mindie.cindy.component.ComponentMetadataManager;

public class ResolverBuilder implements IResolverBuilder {

	////////////////////////
	// VARIABLES
	////////////////

	final private Class<?> converterClass;
	final private Class<?> inputClass;
	final private Class<?> outputClass;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ResolverBuilder(Class<?> converterClass, Class<?> inputClass, Class<?> outputClass) {
		this.converterClass = converterClass;
		this.inputClass = inputClass;
		this.outputClass = outputClass;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public String toString() {
		return this.inputClass + " -> " + this.outputClass;
	}

	@Override
	public IResolver findOrCreateResolver(ComponentInitializer initializer, ComponentBox enclosingBox) {
		IResolver resolver = (IResolver)enclosingBox.findComponent(this.converterClass);

		if (resolver == null) {
			resolver = (IResolver)initializer.createComponent(this.converterClass, enclosingBox).getInstance();
		}

		return resolver;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Class<?> getConverterClass() {
		return this.converterClass;
	}

	public Class<?> getOutputClass() {
		return this.outputClass;
	}

	public Class<?> getInputClass() {
		return this.inputClass;
	}

	public boolean isDynamic() {
		return IDynamicResolver.class.isAssignableFrom(this.converterClass);
	}

}
