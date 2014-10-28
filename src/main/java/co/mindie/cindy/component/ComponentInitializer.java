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
import co.mindie.cindy.component.debugger.DebuggerJsonGenerator;
import co.mindie.cindy.exception.CindyException;
import co.mindie.cindy.utils.Initializable;
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
	private ComponentMetadataManager metadataManager;
	private int currentRecursionCallCount;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentInitializer(ComponentMetadataManager metadataManager) {
		this.metadataManager = metadataManager;
		this.components = new HashMap<>();
		this.createdComponents = new ArrayList<>();
	}

	////////////////////////
	// METHODS
	////////////////

	public <T> CreatedComponent<T> addCreatedComponent(T component, ComponentBox box) {
		return this.addCreatedComponent(component, this.metadataManager.getComponentMetadata(component.getClass()), box, (Class<T>)component.getClass());
	}

	private <T> CreatedComponent<T> addCreatedComponent(T objectInstance, ComponentMetadata metadata, ComponentBox enclosingBox, Class<T> cls) {
		if (enclosingBox != null) {
			enclosingBox.addComponent(objectInstance, metadata.getAspects());
		}

		Box boxAnnotation = metadata.getBox();
		ComponentBox innerBox = null;

		if (boxAnnotation != null) {
			if (enclosingBox != null) {
				innerBox = enclosingBox.createChildBox(boxAnnotation.needAspects(), boxAnnotation.rejectAspects(), objectInstance);
			} else {
				innerBox = new ComponentBox(boxAnnotation.needAspects(), boxAnnotation.rejectAspects(), null);
				innerBox.setOwner(objectInstance);
			}
			if (objectInstance instanceof ComponentBoxListener) {
				ComponentBoxListener cindyComponent = (ComponentBoxListener)objectInstance;
				cindyComponent.setInnerBox(innerBox);
			}
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

			this.metadataManager.signalComponentInitialized(objectInstance);
		} catch (RuntimeException e) {
			this.metadataManager.signalComponentInitializationFailed(objectInstance, e);
			throw e;
		} finally {
			this.currentRecursionCallCount--;
		}
	}

	private <T> void injectDependencies(CreatedComponent<T> createdComponent) {
		ComponentMetadata componentMetadata = createdComponent.getMetadata();
		Class<?> objectClass = createdComponent.getCls();
		Object objectInstance = createdComponent.getInstance();
		WireListener objectInstanceAsInitializerListener = objectInstance instanceof WireListener ?
				(WireListener)objectInstance : null;

		this.log("/ Injecting all dependencies of " + objectInstance.getClass().getSimpleName() + " in box " + createdComponent.getCurrentBox());
		this.currentRecursionCallCount++;

		if (objectInstanceAsInitializerListener != null) {
			objectInstanceAsInitializerListener.onWillWire();
		}

		for (ComponentDependency dependency : componentMetadata.getDependencies()) {
			ComponentBox currentBox = createdComponent.getCurrentBox();

			if (dependency.getWire() != null && dependency.getWire().getBox() != null) {
				Box box = dependency.getWire().getBox();

				currentBox = currentBox.createChildBox(box.needAspects(), box.rejectAspects(), objectInstance);
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
				dependencyInstance = currentBox.findComponent(dependencyClass, dependency.getSearchScope());
			}

			if (dependencyInstance == null && dependency.getCreationBox() != CreationBox.NO_CREATION) {
				try {
					if (dependency.getCreationBox() == CreationBox.PARENT_BOX) {
						currentBox = currentBox.getSuperBox();
					}

					CreatedComponent createdDependencyComponent = this.createComponentInternal(currentBox, (Class)dependencyClass);

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

	private <T> CreatedComponent<T> createComponentInternal(ComponentBox enclosingBox, Class<T> objectClass) {
		this.currentRecursionCallCount++;
		try {
			ComponentMetadata metadata = this.metadataManager.getCompatibleMetadata(objectClass);

			if (metadata == null) {
				return null;
			}

			T objectInstance = (T)metadata.createInstance();

			this.log("+ Created instance of " + objectClass.getSimpleName() + " resolved to " + metadata.getComponentClass().getSimpleName() + " in context " + enclosingBox);

			CreatedComponent<T> createdComponent =  this.addCreatedComponent(objectInstance, metadata, enclosingBox, objectClass);
			this.metadataManager.signalComponentCreated(objectInstance);

			return createdComponent;
		} finally {
			this.currentRecursionCallCount--;
		}
	}

	public <T> CreatedComponent<T> createComponent(ComponentBox enclosingBox, Class<T> objectClass) {
		try {
			return this.createComponentInternal(enclosingBox, objectClass);
		} catch (RuntimeException e) {
			DebuggerJsonGenerator.generateToTempFileSafe(enclosingBox);

			throw e;
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
		for (CreatedComponent createdComponent : this.createdComponents) {
			this.injectDependencies(createdComponent);
		}

		for (CreatedComponent createdComponent : this.createdComponents) {
			this.init(createdComponent);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public ComponentMetadataManager getMetadataManager() {
		return metadataManager;
	}
}
