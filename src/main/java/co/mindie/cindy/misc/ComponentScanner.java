/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.misc
// ComponentScanner.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 7, 2014 at 11:05:08 AM
////////

package co.mindie.cindy.misc;

import java.lang.annotation.Annotation;
import java.util.Set;

import co.mindie.cindy.CindyApp;
import co.mindie.cindy.automapping.Resolver;
import me.corsin.javatools.misc.Action2;

import org.reflections.Reflections;

import co.mindie.cindy.automapping.Component;
import co.mindie.cindy.automapping.Controller;
import co.mindie.cindy.modelconverter.IResolver;

public class ComponentScanner {

	////////////////////////
	// VARIABLES
	////////////////

	private Reflections reflections;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentScanner(String classPath) {
		Reflections.log = null;
		this.reflections = new Reflections(classPath);
	}

	////////////////////////
	// METHODS
	////////////////

	public void addComponents(CindyApp application) {
		this.scanClass(Component.class, Object.class, (matchedType, service) -> {
			application.getComponentMetadataManager().loadComponent(matchedType);
		});

		this.scanClass(Controller.class, Object.class, (matchedType, service) -> {
			application.addController(matchedType, service.basePath(), service.useReusePool());
		});

		this.scanClass(Resolver.class, IResolver.class, (matchedType, modelConverter) -> {
			application.getComponentMetadataManager().loadComponent(matchedType);

			for (Class<?> inputClass : modelConverter.managedInputClasses()) {
				for (Class<?> outputClass : modelConverter.managedOutputClasses()) {
					application.getModelConverterManager().addConverter(matchedType, inputClass, outputClass, modelConverter.isDefaultForInputTypes());
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private <T, T2 extends Annotation> void scanClass(Class<T2> annotationType, Class<T> outputClass, Action2<Class<T>, T2> classHandler) {
		Set<Class<?>> matchedTypes = this.reflections.getTypesAnnotatedWith(annotationType);

		for (Class<?> matchedType : matchedTypes) {
			if (outputClass.isAssignableFrom(matchedType)) {
//				try {
				classHandler.run((Class<T>) matchedType, matchedType.getAnnotation(annotationType));
//				} catch (Throwable t) {
//					t.printStackTrace();
//					System.err.println("=== Unable to instantiate " + annotationType.getSimpleName().toLowerCase() + " " + matchedType.getSimpleName() + ": " + t.getMessage());
//					throw new RuntimeException(t);
//				}
			} else {
				System.err.println("=== Found a class " + matchedType.getSimpleName() + " with a " + annotationType.getSimpleName() +
						" annotation but it does not implement " + outputClass.getSimpleName());
			}
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
