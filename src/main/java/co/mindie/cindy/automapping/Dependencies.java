/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// Service.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 4, 2014 at 1:42:02 PM
////////

package co.mindie.cindy.automapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Dependencies {

	/**
	 * @return the class that this class depends. This adds this class as the returned class dependencies.
	 */
	Class<?> dependentClass() default void.class;

	/**
	 * @return the classes that depend on this class. This adds these classes in the class dependencies.
	 */
	Class<?>[] dependenciesClasses() default {};

	/**
	 * @return the ComponentBox's SearchScope to use for searching dependencies.
	 */
	SearchScope searchScope() default SearchScope.GLOBAL;

}
