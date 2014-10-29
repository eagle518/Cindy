/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.component
// ComponentInitializer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 7, 2014 at 4:24:57 PM
////////

package co.mindie.cindy.component;

import co.mindie.cindy.automapping.Box;
import co.mindie.cindy.automapping.CreationBox;
import co.mindie.cindy.automapping.SearchScope;
import co.mindie.cindy.component.box.ComponentBox;
import co.mindie.cindy.exception.CindyException;
import co.mindie.cindy.utils.Initializable;
import me.corsin.javatools.misc.NullArgumentException;
import me.corsin.javatools.string.Strings;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentInitializer implements Initializable {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(ComponentInitializer.class);

	private Map<Object, CreatedComponent<?>> components;
	private List<CreatedComponent> createdComponents;
	private List<ComponentBox> createdBoxes;
	private ComponentMetadataManager metadataManager;
	private int currentRecursionCallCount;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentInitializer(ComponentMetadataManager metadataManager) {
		this.metadataManager = metadataManager;
		this.components = new HashMap<>();
		this.createdComponents = new ArrayList<>();
		this.createdBoxes = new ArrayList<>();
	}

	////////////////////////
	// METHODS
	////////////////

	/**
	 * Add an already created Component in the given box and append it to the components to
	 * initialize. Calling init() on the ComponentInitializer will then inject its dependencies and initialize all the
	 * created components. This is useful if you want to initialize an object that was created outside of the
	 * ComponentInitializer scope.
	 * @param component The component to add
	 * @param box The box that will be the enclosing box of the component.
	 * @return The CreatedComponent model
	 */
	public <T> CreatedComponent<T> addCreatedComponent(T component, ComponentBox box) {
		if (box == null) {
			throw new NullArgumentException("box");
		}

		Class<T> componentClass = (Class<T>)component.getClass();
		ComponentMetadata componentMetadata = this.metadataManager.getComponentMetadata(componentClass);

		if (componentMetadata == null) {
			throw new CindyException("The component " + component.getClass() + " has not been loaded");
		}

		return this.addCreatedComponent(component, componentMetadata, box, componentClass);
	}

	public <T> CreatedComponent<T> createComponent(Class<T> objectClass, ComponentBox enclosingBox) {
		this.currentRecursionCallCount++;
		try {
			ComponentMetadata metadata = this.metadataManager.getCompatibleMetadata(objectClass);

			if (metadata == null) {
				return null;
			}

			T objectInstance = (T)metadata.createInstance();

			this.log("+ Created instance of " + objectClass.getSimpleName() + " resolved to " + metadata.getComponentClass().getSimpleName() + " in context " + enclosingBox);

			CreatedComponent<T> createdComponent =  this.addCreatedComponent(objectInstance, metadata, enclosingBox, objectClass);

			return createdComponent;
		} finally {
			this.currentRecursionCallCount--;
		}
	}

	private <T> CreatedComponent<T> addCreatedComponent(T objectInstance, ComponentMetadata metadata, ComponentBox enclosingBox, Class<T> cls) {
		if (enclosingBox != null) {
			enclosingBox.addComponent(objectInstance, metadata.getAspects());
		}

		Box boxAnnotation = metadata.getBox();
		ComponentBox innerBox = null;

		if (boxAnnotation != null) {
			if (enclosingBox != null) {
				innerBox = enclosingBox.createChildBox(boxAnnotation.needAspects(), boxAnnotation.rejectAspects(), objectInstance, boxAnnotation.readOnly());
			} else {
				innerBox = ComponentBox.create(boxAnnotation.needAspects(), boxAnnotation.rejectAspects(), null, boxAnnotation.readOnly());
				innerBox.setOwner(objectInstance);
			}

			this.createdBoxes.add(innerBox);
		}

		CreatedComponent<T> createdComponent = new CreatedComponent<T>(objectInstance, metadata, enclosingBox, innerBox, cls);
		this.createdComponents.add(createdComponent);

		this.components.put(objectInstance, createdComponent);

		return createdComponent;
	}

	private void log(String format, Object... params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < this.currentRecursionCallCount; i++) {
			sb.append(' ');
		}

		sb.append(format);

		LOGGER.trace(Strings.format(sb.toString(), params));
	}

	private void initComponent(ComponentBox componentBox, ComponentMetadata componentMetadata, Class<?> objectClass, Object objectInstance) {
		this.log("/ Initializing " + objectInstance.getClass().getSimpleName());

		this.currentRecursionCallCount++;
		try {
			if (objectInstance instanceof Initializable) {
				Initializable initializable = (Initializable) objectInstance;

				initializable.init();
			}
		} finally {
			this.currentRecursionCallCount--;
		}
	}

	private <T> void injectDependencies(CreatedComponent<T> createdComponent) {
		ComponentMetadata componentMetadata = createdComponent.getMetadata();
		Object objectInstance = createdComponent.getInstance();
		Class<?> objectClass = objectInstance.getClass();
		WireListener objectInstanceAsInitializerListener = objectInstance instanceof WireListener ?
				(WireListener)objectInstance : null;

		this.log("/ Injecting all dependencies of " + objectClass.getSimpleName() + " in box " + createdComponent.getCurrentBox());
		this.currentRecursionCallCount++;

		ComponentBox currentBox = createdComponent.getCurrentBox();

		if (currentBox == null) {
			throw new CindyException("On component " + objectClass + ": No current box found (the component has " +
					"neither a inner box nor an enclosing box)");
		}

		if (objectInstanceAsInitializerListener != null) {
			objectInstanceAsInitializerListener.onWillWire();
		}

		for (ComponentDependency dependency : componentMetadata.getDependencies()) {
			currentBox = createdComponent.getCurrentBox();


			if (dependency.getWire() != null && dependency.getWire().getBox() != null) {
				Box box = dependency.getWire().getBox();

				currentBox = currentBox.createChildBox(box.needAspects(), box.rejectAspects(), objectInstance, box.readOnly());
				this.createdBoxes.add(currentBox);
			}

			Class<?> dependencyClass = dependency.getComponentClass();

			this.log("- Injecting dependency " + dependencyClass.getSimpleName() + " in creationScope " + dependency.getCreationBox() + " with searchScope " + dependency.getSearchScope());

			Object dependencyInstance = null;

			if (dependencyClass == ComponentMetadataManager.class) {
				dependencyInstance = componentMetadata.getManager();
			} else if (dependency.isList()) {
				List<?> list = currentBox.findComponents(dependencyClass, dependency.getSearchScope());
				if (list == null) {
					list = new ArrayList<>();
				} else {
					list = new ArrayList<>(list);
				}
				dependencyInstance = list;
			} else if (dependency.getSearchScope() != SearchScope.NO_SEARCH) {
				try {
					dependencyInstance = currentBox.findComponent(dependencyClass, dependency.getSearchScope());
				} catch (Exception e) {
					throw new CindyException("On component " + objectClass + " and dependency " + dependencyClass + ": Failed to search component in ComponentBox", e);
				}
			}

			if (dependencyInstance == null && dependency.getCreationBox() != CreationBox.NO_CREATION) {
				try {
					if (dependency.getCreationBox() == CreationBox.PARENT_BOX) {
						currentBox = currentBox.getSuperBox();
					}

					CreatedComponent createdDependencyComponent = this.createComponent((Class) dependencyClass, currentBox);

					if (createdDependencyComponent != null) {
						dependencyInstance = createdDependencyComponent.getInstance();
					}

					if (dependencyInstance == null && dependency.isRequired()) {
						throw new Exception("Unable to find candidate on a required dependency");
					}
				} catch (Exception e) {
					throw new CindyException("Failed to create dependency " + dependencyClass + " for " + objectClass + " using creationScope " + dependency.getCreationBox(), e);
				}
			}

			if (dependency.getWire() != null) {
				dependency.getWire().set(objectInstance, dependencyInstance);
			}

			if (dependencyInstance != null) {
				createdComponent.addDependency(dependencyInstance);
			}
		}

		for (Wire wireCore : componentMetadata.getWireCores()) {
			Class<?> wireType = wireCore.getFieldType();

			if (wireType == ComponentMetadataManager.class) {
				wireCore.set(objectInstance, this.metadataManager);
			} else if (wireType == ComponentBox.class) {
				String context = wireCore.getContext();

				if (context == null) {
					throw new CindyException("A WiredCore to a ComponentBox needs a context to know which box should be wired.");
				}

				if (context.equals("this")) {
					wireCore.set(objectInstance, createdComponent.getCurrentBox());
				} else if (context.equals("super")) {
					wireCore.set(objectInstance, createdComponent.getEnclosingBox());
				} else {
					throw new CindyException("Referencing a ComponentBox other than 'this' and 'super' is not supported yet.");
				}
			} else if (wireType == ComponentMetadata.class) {
				String context = wireCore.getContext();

				if (context == null) {
					throw new CindyException("A WiredCore to a ComponentMetadata needs a context to know which metadata should be wired.");
				}

				if (context.equals("this")) {
					wireCore.set(objectInstance, componentMetadata);
				} else {
					throw new CindyException("Referencing a ComponentMetata other than 'this' is not supported yet.");
				}
			}
		}

		this.currentRecursionCallCount--;

		if (objectInstanceAsInitializerListener != null) {
			objectInstanceAsInitializerListener.onWired();
		}

	}

	private <T> void init(CreatedComponent<T> createdComponent) {
		if (!createdComponent.isInitialized()) {
			createdComponent.setInitialized(true);

			for (Object dependency : createdComponent.getDependencies()) {
				CreatedComponent createdDependency = this.components.get(dependency);

				if (createdDependency != null) {
					this.init(createdDependency);
				}
			}

			this.initComponent(createdComponent.getEnclosingBox(), createdComponent.getMetadata(), createdComponent.getCls(), createdComponent.getInstance());
		}
	}

	@Override
	public void init() {
		for (int i = 0; i < this.createdComponents.size(); i++) {
			this.injectDependencies(this.createdComponents.get(i));
		}

		for (CreatedComponent createdComponent : this.createdComponents) {
			this.init(createdComponent);
		}

		for (ComponentBox componentBox : this.createdBoxes) {
			componentBox.init();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public ComponentMetadataManager getMetadataManager() {
		return metadataManager;
	}
}
