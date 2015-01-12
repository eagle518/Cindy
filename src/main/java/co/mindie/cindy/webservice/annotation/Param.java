/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// Service.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 4, 2014 at 1:42:02 PM
////////

package co.mindie.cindy.webservice.annotation;

import co.mindie.cindy.webservice.controller.ParamSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

	/**
	 * @return whether the request should fail if this parameter was not resolved.
	 */
	boolean required() default true;

	/**
	 * @return the api name of this parameter. By default it uses the loaded component that implement IParameterNameResolver.
	 */
	String name() default "";

	/**
	 * @return where the parameter value should be fetched.
	 */
	ParamSource source() default ParamSource.AUTO;

	/**
	 * @return the options to send to the resolvers for resolving this parameter.
	 */
	ResolverOption[] resolverOptions() default {};

}
