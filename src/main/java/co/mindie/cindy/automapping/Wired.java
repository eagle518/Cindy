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
@Target(ElementType.FIELD)
public @interface Wired {

	/**
	 * If the wiring fails, this property set to true will make the
	 * component creation to fail. This parameter is ignored if the type
	 * is a Collection
	 */
	boolean required() default true;

	/**
	 * On which scope the wiring should try to find the instance. A GLOBAL scope
	 * will try to find an instance from the current ComponentContext. A LOCAL scope
	 * will only try to find an instance that has been added in the CindyComponent
	 *
	 * @return
	 */
	SearchScope searchScope() default SearchScope.UNDEFINED;

	CreationScope creationScope() default CreationScope.UNDEFINED;

	/**
	 * Which class to use for wiring. This is necessary if the actual type is a collection
	 * or if you want to specialize a type to use
	 *
	 * @return
	 */
	Class<?> fieldClass() default void.class;


}
