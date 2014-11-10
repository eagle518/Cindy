/////////////////////////////////////////////////
// Project : exiled-masterserver
// Package : com.kerious.exiled.masterserver.api
// Mapped.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 11, 2013 at 3:20:04 PM
////////

package co.mindie.cindy.automapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Endpoint {

	String path() default "";

	HttpMethod httpMethod() default HttpMethod.GET;

	String[] requiredPermissions() default {};

	Class<?> responseWriterClass() default void.class;

	boolean resolveOutput() default true;

	ResolverOption[] outputResolverOptions() default {};

}
