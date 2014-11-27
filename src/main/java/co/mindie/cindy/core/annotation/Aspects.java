/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// Service.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 4, 2014 at 1:42:02 PM
////////

package co.mindie.cindy.core.annotation;

import co.mindie.cindy.core.component.Aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Aspects {

	/**
 	 * @return the ComponentAspect's that this component has. This gives insight on whether this component
	 * can be added in a particular ComponentBox.
	 */
	Aspect[] aspects() default {};

}
