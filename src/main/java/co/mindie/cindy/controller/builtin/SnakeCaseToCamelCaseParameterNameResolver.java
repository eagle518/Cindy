/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// SnakeCaseToCamelCaseParameterNameResolver.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2014 at 12:18:37 PM
////////

package co.mindie.cindy.controller.builtin;

import co.mindie.cindy.automapping.Component;
import co.mindie.cindy.automapping.CreationResolveMode;
import co.mindie.cindy.automapping.Singleton;
import co.mindie.cindy.component.CindyComponent;
import co.mindie.cindy.controller.manager.IParameterNameResolver;

@Component(creationResolveMode = CreationResolveMode.FALLBACK)
public class SnakeCaseToCamelCaseParameterNameResolver extends CindyComponent implements IParameterNameResolver {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public SnakeCaseToCamelCaseParameterNameResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public String javaParameterNameToApiName(String name) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0, length = name.length(); i < length; i++) {
			char c = name.charAt(i);

			if (Character.isUpperCase(c)) {
				c = Character.toLowerCase(c);
				if (i != 0) {
					sb.append('_');
				}
			}

			sb.append(c);
		}


		return sb.toString();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
