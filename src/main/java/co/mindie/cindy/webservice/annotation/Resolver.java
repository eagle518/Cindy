/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// Service.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 4, 2014 at 1:42:02 PM
////////

package co.mindie.cindy.webservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A resolver takes an input and resolve it to an output.
 * It must implements the interface IResolver.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Resolver {

	/**
	 * @return What are the input classes the resolver manages. If empty, the ResolverManager
	 * will use the generic type parameter to find this value.
	 *
	 */
	Class<?>[] managedInputClasses() default {};

	/**
	 * @return What are the output classes the resolver manages. If empty, the ResolverManager
	 * will use the generic type parameter to find this value.
	 *
	 */
	Class<?>[] managedOutputClasses() default {};

	boolean isDefaultForInputTypes() default false;

}
