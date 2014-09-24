/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.component
// ComponentInitializer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 7, 2014 at 4:24:57 PM
////////

package co.mindie.cindy.component;

import co.mindie.cindy.CindyApp;
import co.mindie.cindy.automapping.CreationScope;
import co.mindie.cindy.automapping.SearchScope;
import co.mindie.cindy.component.debugger.DebuggerJsonGenerator;
import co.mindie.cindy.configuration.Configurations;
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

	private Map<Object, CreatedComponent> components;
	private List<CreatedComponent> createdComponents;
	private ComponentMetadataManager metadataManager;
	private List<ComponentContext> baseComponentContexts;
	private int currentRecursionCallCount;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentInitializer(ComponentMetadataManager metadataManager) {
		this.metadataManager = metadataManager;
		this.components = new HashMap<Object, CreatedComponent>();
		this.createdComponents = new ArrayList<>();
		this.baseComponentContexts = new ArrayList<>();
	}

	////////////////////////
	// METHODS
	////////////////

	public void addCreatedComponent(Object component, ComponentContext context) {
		if (!this.baseComponentContexts.contains(context)) {
			this.baseComponentContexts.add(context);
		}

		this.addCreatedComponent(component, this.metadataManager.getComponentMetadata(component.getClass()), context, component.getClass());
	}

	private void addCreatedComponent(Object objectInstance, ComponentMetadata metadata, ComponentContext context, Class<?> cls) {
		if (objectInstance instanceof CindyComponent) {
			CindyComponent component = (CindyComponent) objectInstance;
			component.setApplication(this.metadataManager.getApplication());
		}

		context.addComponent(objectInstance);

		CreatedComponent createdComponent = new CreatedComponent(objectInstance, metadata, context, cls);
		this.createdComponents.add(createdComponent);

		this.components.put(objectInstance, createdComponent);
	}

	private void log(String format, Object... params) {
		StringBuilder sb = new StringBuilder();

		if (!this.metadataManager.getApplication().isInitialized()) {
			for (int i = 0; i < this.currentRecursionCallCount; i++) {
				sb.append(' ');
			}
		}

		sb.append(format);

		LOGGER.trace(Strings.format(sb.toString(), params));
	}

	private void initComponent(ComponentContext componentContext, ComponentMetadata componentMetadata, Class<?> objectClass, Object objectInstance) {
		this.log("/ Initializing " + objectInstance.getClass().getSimpleName());

		this.currentRecursionCallCount++;
		try {
			if (objectInstance instanceof Initializable) {
				Initializable initializable = (Initializable) objectInstance;

				if (!initializable.isInitialized()) {
					initializable.init();
				}
			}

			this.metadataManager.signalComponentInitialized(objectInstance);
		} catch (RuntimeException e) {
			this.metadataManager.signalComponentInitializationFailed(objectInstance, e);
			throw e;
		} finally {
			this.currentRecursionCallCount--;
		}
	}

	private void injectDependencies(CreatedComponent createdComponent) {
		ComponentContext componentContext = createdComponent.getContext();
		ComponentMetadata componentMetadata = createdComponent.getMetadata();
		Class<?> objectClass = createdComponent.getCls();
		Object objectInstance = createdComponent.getInstance();
		ComponentContext subContext = null;

		this.log("/ Injecting all dependencies of " + objectInstance.getClass().getSimpleName() + " in context " + componentContext);
		this.currentRecursionCallCount++;

		for (ComponentDependency dependency : componentMetadata.getDependencies()) {
			Class<?> dependencyClass = dependency.getComponentClass();

			this.log("- Injecting dependency " + dependencyClass.getSimpleName() + " in creationScope " + dependency.getCreationScope() + " with searchScope " + dependency.getSearchScope());

			Object dependencyInstance = null;

			if (dependency.isList()) {
				List<?> list = componentContext.findComponents(dependencyClass, dependency.getSearchScope());
				if (list == null) {
					list = new ArrayList<>();
				} else {
					list = new ArrayList<>(list);
				}
				dependencyInstance = list;
			} else if (dependency.getSearchScope() != SearchScope.NO_SEARCH) {
				dependencyInstance = componentContext.findComponent(dependencyClass, dependency.getSearchScope());
			}

			ComponentContext dependencyContext = null;
			if (dependencyInstance == null && dependency.getCreationScope() != CreationScope.NO_CREATION) {
				try {
					switch (dependency.getCreationScope()) {
						case LOCAL:
							dependencyContext = componentContext;
							break;
						case SUB_CONTEXT:
							if (subContext == null) {
								subContext = componentContext.createSubComponentContext();
								subContext.setOwner(objectInstance);
								componentContext.addChildComponentContext(subContext);
							}

							dependencyContext = subContext;
							break;
						case ISOLATED:
							dependencyContext = componentContext.createSubComponentContext();
							dependencyContext.setOwner(objectInstance);
							componentContext.addChildComponentContext(dependencyContext);
							break;
						default:
							break;
					}

					dependencyInstance = this.createComponentInternal(dependencyContext, dependencyClass);

					if (dependencyInstance == null && dependency.isRequired()) {
						throw new Exception("Unable to find candidate");
					}
				} catch (Exception e) {
					throw new CindyException("Failed to create dependency " + dependencyClass + " for " + objectClass + " using creationScope " + dependency.getCreationScope(), e);
				}
			}

			if (dependency.getWire() != null) {
				dependency.getWire().set(objectInstance, dependencyInstance);
			}

			if (dependencyInstance != null) {
				createdComponent.addDependency(dependencyInstance);
			}
		}
		this.currentRecursionCallCount--;
	}

	private Object createComponentInternal(ComponentContext componentContext, Class<?> objectClass) {
		this.currentRecursionCallCount++;
		try {
			ComponentMetadata metadata = this.metadataManager.getCompatibleMetadata(objectClass);

			if (metadata == null) {
				return null;
			}

			Object objectInstance = metadata.createInstance();

			this.log("+ Created instance of " + objectClass.getSimpleName() + " resolved to " + metadata.getComponentClass().getSimpleName() + " in context " + componentContext);

			this.addCreatedComponent(objectInstance, metadata, componentContext, objectClass);
			this.metadataManager.signalComponentCreated(objectInstance);

			return objectInstance;
		} finally {
			this.currentRecursionCallCount--;
		}
	}

	public Object createComponent(ComponentContext componentContext, Class<?> objectClass) {
		if (!this.baseComponentContexts.contains(componentContext)) {
			this.baseComponentContexts.add(componentContext);
		}
		try {
			return this.createComponentInternal(componentContext, objectClass);
		} catch (RuntimeException e) {
			DebuggerJsonGenerator.generateToTempFileSafe(componentContext);

			throw e;
		}
	}

	private void init(CreatedComponent createdComponent) {
		if (!createdComponent.isInitialized()) {
			createdComponent.setInitialized(true);

			for (Object dependency : createdComponent.getDependencies()) {
				CreatedComponent createdDependency = this.components.get(dependency);

				if (createdDependency != null) {
					this.init(createdDependency);
				}
			}

			this.initComponent(createdComponent.getContext(), createdComponent.getMetadata(), createdComponent.getCls(), createdComponent.getInstance());
		}
	}

	@Override
	public void init() {
		RuntimeException thrownException = null;
		try {
			for (int i = 0; i < this.createdComponents.size(); i++) {
				this.injectDependencies(this.createdComponents.get(i));
			}

			for (CreatedComponent createdComponent : this.createdComponents) {
				this.init(createdComponent);
			}
		} catch (RuntimeException e) {
			thrownException = e;
		}

		boolean shouldWriteDotDebug = thrownException != null;

		if (!shouldWriteDotDebug) {
			shouldWriteDotDebug = this.isWriteDotDebugEnabled();
		}

		if (shouldWriteDotDebug) {
			for (ComponentContext baseComponentContext : this.baseComponentContexts) {
				DebuggerJsonGenerator.generateToTempFileSafe(baseComponentContext);
			}
		}

		if (thrownException != null) {
			throw thrownException;
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public boolean isInitialized() {
		return false;
	}

	public boolean isWriteDotDebugEnabled() {
		CindyApp app = this.metadataManager.getApplication();
		if (app != null && app.getConfiguration() != null) {
			return app.getConfiguration().getBoolean(Configurations.WRITE_JSON_DEBUG_ON_COMPONENT_CREATION, false);
		}

		return false;
	}
}
