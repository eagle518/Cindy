/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.misc
// ComponentScanner.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 7, 2014 at 11:05:08 AM
////////

package co.mindie.cindy.misc;

import co.mindie.cindy.CindyApp;
import co.mindie.cindy.automapping.Component;
import co.mindie.cindy.automapping.Controller;
import co.mindie.cindy.automapping.CreationScope;
import co.mindie.cindy.automapping.Resolver;
import co.mindie.cindy.automapping.SearchScope;
import co.mindie.cindy.automapping.Singleton;
import co.mindie.cindy.automapping.Worker;
import co.mindie.cindy.component.ComponentMetadata;
import co.mindie.cindy.resolver.IResolver;
import me.corsin.javatools.misc.Action2;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ComponentScanner {

	////////////////////////
	// VARIABLES
	////////////////

	private Reflections reflections;
	private boolean shouldScanComponents;
	private boolean shouldScanControllers;
	private boolean shouldScanResolvers;
	private boolean shouldScanWorkers;
	private boolean shouldScanSingletons;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentScanner(String classPath) {
		Reflections.log = null;
		this.reflections = new Reflections(classPath);

		this.shouldScanComponents = true;
		this.shouldScanControllers = true;
		this.shouldScanResolvers = true;
		this.shouldScanWorkers = true;
		this.shouldScanSingletons = true;
	}

	////////////////////////
	// METHODS
	////////////////

	public void addComponents(CindyApp application) {
		final ComponentMetadata applicationMetadata = application.getComponentMetadataManager().loadComponent(application.getClass());

		if (this.shouldScanSingletons) {
			this.scanClass(Singleton.class, Object.class, (matchedType, service) -> {
				application.getComponentMetadataManager().loadComponent(matchedType);
//				if (!applicationMetadata.hasDependency(matchedType)) {
//					applicationMetadata.addDependency(matchedType, true, false, SearchScope.NO_SEARCH, CreationScope.LOCAL);
//				}
			});
		}

		if (this.shouldScanComponents) {
			this.scanClass(Component.class, Object.class, (matchedType, service) -> {
				application.getComponentMetadataManager().loadComponent(matchedType);
			});
		}

		if (this.shouldScanWorkers) {
			this.scanClass(Worker.class, Object.class, (matchedType, service) -> {
				application.getComponentMetadataManager().loadComponent(matchedType);
				if (!applicationMetadata.hasDependency(matchedType)) {
					applicationMetadata.addDependency(matchedType, true, false, SearchScope.NO_SEARCH, CreationScope.LOCAL);
				}
			});
		}

		if (this.shouldScanControllers) {
			this.scanClass(Controller.class, Object.class, (matchedType, service) -> {
				application.addController(matchedType, service.basePath());
			});
		}

		if (this.shouldScanResolvers) {
			this.scanClass(Resolver.class, IResolver.class, (matchedType, modelConverter) -> {
				application.getComponentMetadataManager().loadComponent(matchedType);

				for (Class<?> inputClass : modelConverter.managedInputClasses()) {
					for (Class<?> outputClass : modelConverter.managedOutputClasses()) {
						application.getModelConverterManager().addConverter(matchedType, inputClass, outputClass, modelConverter.isDefaultForInputTypes());
					}
				}
			});
		}
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

	public boolean isShouldScanComponents() {
		return shouldScanComponents;
	}

	public void setShouldScanComponents(boolean shouldScanComponents) {
		this.shouldScanComponents = shouldScanComponents;
	}

	public boolean isShouldScanControllers() {
		return shouldScanControllers;
	}

	public void setShouldScanControllers(boolean shouldScanControllers) {
		this.shouldScanControllers = shouldScanControllers;
	}

	public boolean isShouldScanResolvers() {
		return shouldScanResolvers;
	}

	public void setShouldScanResolvers(boolean shouldScanResolvers) {
		this.shouldScanResolvers = shouldScanResolvers;
	}

	public boolean isShouldScanWorkers() {
		return shouldScanWorkers;
	}

	public void setShouldScanWorkers(boolean shouldScanWorkers) {
		this.shouldScanWorkers = shouldScanWorkers;
	}

	public boolean isShouldScanSingletons() {
		return shouldScanSingletons;
	}

	public void setShouldScanSingletons(boolean shouldScanSingletons) {
		this.shouldScanSingletons = shouldScanSingletons;
	}
}
