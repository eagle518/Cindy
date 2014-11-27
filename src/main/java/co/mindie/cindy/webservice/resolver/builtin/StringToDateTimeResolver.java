/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver.builtin
// StringToDateTimeResolver.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 28, 2014 at 1:44:50 PM
////////

package co.mindie.cindy.webservice.resolver.builtin;

import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.webservice.annotation.Resolver;
import co.mindie.cindy.webservice.resolver.IResolver;
import co.mindie.cindy.webservice.resolver.ResolverContext;
import org.joda.time.DateTime;

@Load(creationPriority = -1)
@Resolver(managedInputClasses = String.class, managedOutputClasses = DateTime.class)
public class StringToDateTimeResolver implements IResolver<String, DateTime> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public StringToDateTimeResolver() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public DateTime resolve(String input, Class<?> expectedOutputType, ResolverContext resolverContext) {
		if (input == null) {
			return null;
		}

		return DateTime.parse(input);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
