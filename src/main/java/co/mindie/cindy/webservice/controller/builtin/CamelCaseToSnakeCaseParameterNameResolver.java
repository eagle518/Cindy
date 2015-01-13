/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// SnakeCaseToCamelCaseParameterNameResolver.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2014 at 12:18:37 PM
////////

package co.mindie.cindy.webservice.controller.builtin;

import co.mindie.cindy.webservice.controller.manager.IParameterNameResolver;
import me.corsin.javatools.string.Strings;

public class CamelCaseToSnakeCaseParameterNameResolver implements IParameterNameResolver {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CamelCaseToSnakeCaseParameterNameResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public String javaParameterNameToApiName(String name) {
		return Strings.camelCaseToSnakeCase(name);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
