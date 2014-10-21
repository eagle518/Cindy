/////////////////////////////////////////////////
// Project : Mindie WebService
// Package : co.mindie.webservice.api
// AbstractModelConverter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Feb 13, 2014 at 1:10:44 PM
////////

package co.mindie.cindy.resolver;

import org.apache.log4j.Logger;

public abstract class Resolver<Input, Output> implements IResolver<Input, Output> {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(Resolver.class);
	private final Class<Input> managedInputClass;
	private final Class<Output> outputClass;
	private boolean cacheEnabled;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Resolver(Class<Input> managedInputClass, Class<Output> outputClass) {
		LOGGER.trace(this.getClass() + " created with inputClass=" + managedInputClass + ", outputClass=" + outputClass);
		this.managedInputClass = managedInputClass;
		this.outputClass = outputClass;
		this.cacheEnabled = false;
	}

	////////////////////////
	// METHODS
	////////////////

	protected abstract Output doConvert(Input input);

	public Output resolve(Input input) {
		return this.resolve(input, null, 0);
	}

	@Override
	public Output resolve(Input input, Class<?> expectedOutputType, int options) {
		return this.doConvert(input);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Class<?> getManagedInputClass() {
		return this.managedInputClass;
	}

	public Class<?> getOutputClass() {
		return this.outputClass;
	}

	public boolean isCacheEnabled() {
		return this.cacheEnabled;
	}

	public void setCacheEnabled(boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}
}
