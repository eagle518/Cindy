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

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface Box {

	/**
	 * @return the ComponentAspects that every component added in this box must have.
	 * Attempting to add a Component that doesn't have those aspects inside this box
	 * will fail.
	 */
	Aspect[] needAspects() default {};

	/**
	 * @return the ComponentAspects that every component added in this box must NOT have.
	 * Attempting to add a Component that have one of those aspects inside this box
	 * will fail.
	 */
	Aspect[] rejectAspects() default { Aspect.SINGLETON };

	/**
	 * @return Whether the Box accepts insert operations after the initializing has been done.
	 * Allowing write operations will make this box thread safe. Most of the time you typically
	 * only need a read only box.
	 */
	boolean readOnly() default true;
}
