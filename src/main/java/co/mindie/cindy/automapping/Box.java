/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// Service.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 4, 2014 at 1:42:02 PM
////////

package co.mindie.cindy.automapping;

import co.mindie.cindy.component.ComponentAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface Box {

	/**
	 * @return the ComponentAspects that every component added in this box must have.
	 * Attempting to add a Component that doesn't have those aspects inside this box
	 * will fail.
	 */
	ComponentAspect[] needAspects() default {};

	/**
	 * @return the ComponentAspects that every component added in this box must NOT have.
	 * Attempting to add a Component that have one of those aspects inside this box
	 * will fail.
	 */
	ComponentAspect[] rejectAspects() default { ComponentAspect.SINGLETON };

}
