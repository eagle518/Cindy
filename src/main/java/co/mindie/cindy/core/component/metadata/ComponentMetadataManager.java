/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// InstanceTrigger.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 10, 2014 at 2:38:28 PM
////////

package co.mindie.cindy.core.component.metadata;

import co.mindie.cindy.core.component.initializer.CreatedComponent;
import co.mindie.cindy.core.component.box.ComponentBox;
import co.mindie.cindy.core.component.initializer.ComponentInitializer;
import co.mindie.cindy.core.exception.CindyException;
import co.mindie.cindy.core.module.Module;
import me.corsin.javatools.dynamictext.DynamicText;
import me.corsin.javatools.reflect.ClassIndexer;

import java.util.*;

public class ComponentMetadataManager extends ComponentMetadatasHolder {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentMetadataManager(List<Module> loadedModules, Map<Class<?>, ComponentMetadata> metadatas, ClassIndexer<ComponentMetadata> componentIndexer,
									Map<Class<?>, List<ComponentMetadata>> metadatasByAnnotation) {
		super(loadedModules, metadatas, componentIndexer, metadatasByAnnotation);

		this.ensureIntegrity();
	}

	////////////////////////
	// METHODS
	////////////////

	/**
	 * Ensure that each loaded component can effectively be created correctly and is therefore useable.
	 */
	private void ensureIntegrity() {
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
							(dependency.getWire() != null ? "(wire on property " + dependency.getWire().getField() + ") " : "")
							+ "\nLoad context:\n" + metadata.getLoadDescriptionContext()
							;

					message += "\n\nError message: " + (exception == null ? "No compatible loaded candidate was found" : exception.getMessage());
					throw new CindyException(message);
				}
			}

		}
	}

	public ComponentMetadata getCompatibleMetadata(Class<?> objectClass) {
		List<ComponentMetadata> compatibleComponents = this.findCompatibleComponentsForClass(objectClass);

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
