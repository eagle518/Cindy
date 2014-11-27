/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.misc
// ComponentScanner.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 7, 2014 at 11:05:08 AM
////////

package co.mindie.cindy.core.component.metadata;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ComponentScanner {

	////////////////////////
	// VARIABLES
	////////////////

	private Reflections reflections;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	static {
		Reflections.log = null;
	}

	public ComponentScanner(String classPath) {
		this.reflections = new Reflections(classPath);
	}

	////////////////////////
	// METHODS
	////////////////

	public <T extends Annotation> Set<Class<?>> findAnnotedTypes(Class<T> annotationType) {
		return this.reflections.getTypesAnnotatedWith(annotationType);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}