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
 * A Singleton is a Component that has the THREAD_SAFE and SINGLETON ComponentAspects and adds itself into
 * the application dependency. This makes the component to be created at the same time where the application gets
 * loaded.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Singleton {

	/**
	 * @return the CreationResolveMode to use for this Component.
	 */
	CreationResolveMode creationResolveMode() default CreationResolveMode.DEFAULT;

}
