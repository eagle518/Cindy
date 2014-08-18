/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// SnakeCaseToCamelCaseParameterNameResolver.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2014 at 12:18:37 PM
////////

package co.mindie.wsframework.controller;

import co.mindie.wsframework.WSApplication;
import co.mindie.wsframework.automapping.Component;
import co.mindie.wsframework.component.WSComponent;

@Component(dependentClass = WSApplication.class)
public class CamelCaseToSnakeCaseParameterNameResolver extends WSComponent implements IParameterNameResolver {

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
		StringBuilder sb = new StringBuilder();

		String[] names = name.split("_");
		for (int i = 0; i < names.length; i++) {
			String currentName = names[i];

			if (i == 0) {
				sb.append(currentName);
			} else {
				for (int j = 0; j < currentName.length(); j++) {
					char c = currentName.charAt(j);

					if (j == 0) {
						c = Character.toUpperCase(c);
					}

					sb.append(c);
				}
			}
		}


		return sb.toString();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
