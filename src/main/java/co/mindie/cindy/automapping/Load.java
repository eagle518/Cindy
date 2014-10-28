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
 * Adding this annotation marks the type as loadable. It will be automatically scanned
 * loaded when searching in packages.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Load {

	/**
	 * @return the priority for creating this component. If multiple component candidates are found for a
	 * given type, this component that has the highest priority will be used.
	 */
	int creationPriority() default 0;

}
