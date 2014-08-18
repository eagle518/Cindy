/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// Service.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 4, 2014 at 1:42:02 PM
////////

package co.mindie.wsframework.automapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {

	Class<?> dependentClass() default void.class;

	Class<?>[] dependenciesClasses() default {};

	SearchScope dependenciesSearchScope() default SearchScope.GLOBAL;

	CreationScope dependenciesCreationScope() default CreationScope.LOCAL;

	boolean asyncInit() default false;
}
