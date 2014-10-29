/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// InstanceTrigger.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 10, 2014 at 2:38:28 PM
////////

package co.mindie.cindy.component;

import co.mindie.cindy.automapping.Load;
import co.mindie.cindy.component.box.ComponentBox;
import co.mindie.cindy.exception.CindyException;
import co.mindie.cindy.misc.ComponentScanner;
import me.corsin.javatools.dynamictext.DynamicText;
import me.corsin.javatools.reflect.ClassIndexer;
import me.corsin.javatools.string.Strings;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.util.*;

public class ComponentMetadataManager {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(ComponentMetadataManager.class);

	private Map<Class<?>, ComponentMetadata> metadatas;
	private ClassIndexer<ComponentMetadata> componentIndexer;
	private Map<Class<?>, List<ComponentMetadata>> metadatasByAnnotation;
	private int currentRecursionCallCount;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentMetadataManager() {
		this.metadatas = new HashMap<>();
		this.componentIndexer = new ClassIndexer<>();
		this.metadatasByAnnotation = new HashMap<>();
	}

	////////////////////////
	// METHODS
	////////////////

	public ComponentMetadata getComponentMetadata(Class<?> objectClass) {
		return this.metadatas.get(objectClass);
	}

	public List<ComponentMetadata> findCompatibleComponentForClass(Class<?> objectClass) {
		return this.componentIndexer.find(objectClass);
	}

	public List<ComponentMetadata> loadComponents(String classPath) {
		ComponentScanner scanner = new ComponentScanner(classPath);

		final List<ComponentMetadata> metadatas = new ArrayList<>();

		for (Class<?> type : scanner.findAnnotedTypes(Load.class)) {
			metadatas.add(this.loadComponent(type));
		}

		return metadatas;
 	}

	/**
	 * Ensure that each loaded component can effectively be created correctly and is therefore useable.
	 */
	public void ensureIntegrity() {
		for (ComponentMetadata metadata : this.metadatas.values()) {

			for (ComponentDependency dependency : metadata.getDependencies()) {
				CindyException exception = null;
				ComponentMetadata compatibleMetadata = null;
				try {
					compatibleMetadata = this.getCompatibleMetadata(dependency.getComponentClass());
				} catch (CindyException e) {
					exception = e;
				}

				if (exception != null || (compatibleMetadata == null && dependency.isRequired())) {
					String message = "" +
							"On " + metadata.getComponentClass() + " and dependency " + dependency.getComponentClass() + " " +
							(dependency.getWire() != null ? "(wire on property " + dependency.getWire().getField() + ") " : "") +
							":\n" + (exception == null ? "No compatible loaded candidate was found" : exception.getMessage())
							;
					throw new CindyException(message);
				}
			}

		}
	}

	/**
	 * @param annotationType The annotation type to search
	 * @return A list containing all the component metadata that has the annotationType
	 */
	public <T extends Annotation> List<ComponentMetadata> getLoadedComponentsWithAnnotation(Class<T> annotationType) {
		List<ComponentMetadata> metadatas = this.metadatasByAnnotation.get(annotationType);

		if (metadatas == null) {
			metadatas = new ArrayList<>();
		}

		return metadatas;
	}

	public ComponentMetadata loadComponent(Class<?> cls) {
		if (!ComponentMetadata.isLoadable(cls)) {
			throw new CindyException("The " + cls + " is not loadable (is abstract or an interface)");
		}

		this.currentRecursionCallCount++;
		try {
			ComponentMetadata componentMetadata = this.getComponentMetadata(cls);

			if (componentMetadata == null) {
				componentMetadata = new ComponentMetadata(this, cls);
				this.metadatas.put(cls, componentMetadata);
				this.componentIndexer.add(componentMetadata, cls);

				this.log("Loaded component {#0}", cls.getSimpleName());
				for (ComponentDependency e : componentMetadata.getDependencies()) {
					if (ComponentMetadata.isLoadable(e.getComponentClass()) && e.getComponentClass().getAnnotation(Load.class) != null) {
						this.loadComponent(e.getComponentClass());
					}
				}

				for (Class<?> annotationClass : componentMetadata.getAnnotationClasses()) {
					List<ComponentMetadata> associatedMetadatas = this.metadatasByAnnotation.get(annotationClass);

					if (associatedMetadatas == null) {
						associatedMetadatas = new ArrayList<>();
						this.metadatasByAnnotation.put(annotationClass, associatedMetadatas);
					}

					associatedMetadatas.add(componentMetadata);
				}
			}

			return componentMetadata;
		} finally {
			this.currentRecursionCallCount--;
		}
	}

	private void log(String format, Object... params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < this.currentRecursionCallCount; i++) {
			sb.append(' ');
		}

		sb.append(format);

		LOGGER.trace(Strings.format(sb.toString(), params));
	}

	public ComponentMetadata getCompatibleMetadata(Class<?> objectClass) {
		List<ComponentMetadata> compatibleComponents = this.findCompatibleComponentForClass(objectClass);

		if (compatibleComponents == null) {
			return null;
		}

		ComponentMetadata highest = null;
		boolean highestIsNotAlone = false;

		for (ComponentMetadata metadata : compatibleComponents) {
			if (highest == null || highest.getCreationPriority() < metadata.getCreationPriority()) {
				highest = metadata;
				highestIsNotAlone = false;
			} else if (highest.getCreationPriority() == metadata.getCreationPriority()) {
				highestIsNotAlone = true;
			}
		}

		if (highestIsNotAlone) {
			DynamicText dt = new DynamicText(
					""
							+ "Too many component candidates found with same priority found for {objectClass}\n"
							+ "Candidates:\n"
							+ "[candidates-> candidate:"
							+ "{candidate.componentClass} (priority: {candidate.creationPriority})\n"
							+ "]"
			);
			dt.put("candidates", compatibleComponents);
			dt.put("objectClass", objectClass);

			throw new CindyException(dt.toString());
		}

		return highest;
	}

	/**
	 * Creates a ComponentInitializer that can create and initialize components
	 */
	public ComponentInitializer createInitializer() {
		return new ComponentInitializer(this);
	}

	/**
	 * Create a single component.
	 * @param componentClass The class of the component to create
	 * @param enclosingBox The box to which the component must be added, or null if it should be orphan.
	 */
	public <T> CreatedComponent<T> createComponent(Class<T> componentClass, ComponentBox enclosingBox) {
		ComponentInitializer initializer = this.createInitializer();
		CreatedComponent<T> createdComponent = initializer.createComponent(componentClass, enclosingBox);

		initializer.init();

		return createdComponent;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Collection<ComponentMetadata> getLoadedComponentMetadatas() {
		return this.metadatas.values();
	}

}
