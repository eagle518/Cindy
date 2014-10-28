/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.misc
// ComponentScanner.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 7, 2014 at 11:05:08 AM
////////

package co.mindie.cindy.misc;

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

	public ComponentScanner(String classPath) {
		Reflections.log = null;
		this.reflections = new Reflections(classPath);
	}

	////////////////////////
	// METHODS
	////////////////

//	public void addComponents(ComponentMetadataManager componentMetadataManager) {
//
//		if (this.shouldScanSingletons) {
//			this.findAnnotedTypes(Singleton.class, Object.class, (matchedType, service) -> {
//				componentMetadataManager.loadComponent(matchedType);
//			});
//		}
//
//		if (this.shouldScanComponents) {
//			this.findAnnotedTypes(Component.class, Object.class, (matchedType, service) -> {
//				componentMetadataManager.loadComponent(matchedType);
//			});
//		}
//
//		if (this.shouldScanWorkers) {
//			this.findAnnotedTypes(Worker.class, Object.class, (matchedType, service) -> {
//				componentMetadataManager.loadComponent(matchedType);
//			});
//		}
//
//		if (this.shouldScanControllers) {
//			this.findAnnotedTypes(Controller.class, Object.class, (matchedType, service) -> {
//				componentMetadataManager.loadComponent(matchedType);
//			});
//		}
//
//		if (this.shouldScanResolvers) {
//			this.findAnnotedTypes(Resolver.class, IResolver.class, (matchedType, modelConverter) -> {
//				componentMetadataManager.loadComponent(matchedType);
//
//				// TODO Should be moved in ResolverManager class
////				for (Class<?> inputClass : modelConverter.managedInputClasses()) {
////					for (Class<?> outputClass : modelConverter.managedOutputClasses()) {
////						application.getResolverManager().addConverter(matchedType, inputClass, outputClass, modelConverter.isDefaultForInputTypes());
////					}
////				}
//			});
//		}
//	}

	public <T extends Annotation> Set<Class<?>> findAnnotedTypes(Class<T> annotationType) {
		return this.reflections.getTypesAnnotatedWith(annotationType);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
