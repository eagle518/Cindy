/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver.builtin
// RequestParameterToStringResolver.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 1:24:52 PM
////////

package co.mindie.cindy.resolver.builtin;

import co.mindie.cindy.automapping.Resolver;
import co.mindie.cindy.controller.manager.RequestParameter;
import co.mindie.cindy.resolver.IResolver;

@Resolver(managedInputClasses = RequestParameter.class, managedOutputClasses = String.class)
public class RequestParameterToStringResolver implements IResolver<RequestParameter, String> {

	////////////////////////
	// VARIABLES
	////////////////

	public static int OPTIONS_ALLOW_NULL = 1;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public RequestParameterToStringResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public String resolve(RequestParameter input, Class<?> expectedOutputType, int options) {
		if (input == null) {
			return null;
		}

		String strValue = input.getStringValue();

//		if (strValue == null && ((options & OPTIONS_ALLOW_NULL) == 0)) {
//			throw new NullArgumentException(input.getName());
//		}

		return strValue;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
