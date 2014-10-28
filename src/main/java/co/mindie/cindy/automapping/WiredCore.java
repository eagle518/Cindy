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

/**
 * Wire an object from the core of Cindy.
 * You can currently wire a ComponentMetadataManager and a ComponentBox.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WiredCore {

	/**
	 * The context on where the core can be found.
	 * On a ComponentBox, it can be "this" to wire the inner box,
	 * "super" to wire the enclosing box, or a property name to wire
	 * the enclosing box of a specific property.
	 * @return the context on where the core can be found.
	 */
	String value() default "this";

}
