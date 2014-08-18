/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.modelconverter.builtin
// RequestParameterToStringResolver.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 1:24:52 PM
////////

package co.mindie.wsframework.modelconverter.builtin;

import java.io.InputStream;

import co.mindie.wsframework.automapping.Resolver;
import co.mindie.wsframework.controllermanager.RequestParameter;
import co.mindie.wsframework.modelconverter.IResolver;

@Resolver(managedInputClasses = RequestParameter.class, managedOutputClasses = InputStream.class)
public class RequestParameterToInputStreamResolver implements IResolver<RequestParameter, InputStream> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public RequestParameterToInputStreamResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public InputStream resolve(RequestParameter input, Class<?> expectedOutputType, int options) {
		if (input == null) {
			return null;
		}

		return input.getInputStream();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
