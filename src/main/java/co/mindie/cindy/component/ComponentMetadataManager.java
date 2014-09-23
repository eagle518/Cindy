/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// InstanceTrigger.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 10, 2014 at 2:38:28 PM
////////

package co.mindie.cindy.component;

import co.mindie.cindy.CindyApp;
import co.mindie.cindy.automapping.CreationResolveMode;
import co.mindie.cindy.automapping.CreationScope;
import co.mindie.cindy.automapping.SearchScope;
import co.mindie.cindy.exception.CindyException;
import me.corsin.javatools.dynamictext.DynamicText;
import me.corsin.javatools.reflect.ClassIndexer;
import me.corsin.javatools.string.Strings;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComponentMetadataManager {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(ComponentMetadataManager.class);

	private IComponentFactoryListener listener;
	private Map<Class<?>, ComponentMetadata> metadatas;
	private ClassIndexer<ComponentMetadata> componentIndexer;
	private int currentRecursionCallCount;
	private CindyApp application;
	private boolean onlyChainLoadIfRequired;
	private boolean selfInjectionDisabledOnAutoLoad;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentMetadataManager() {
		this.metadatas = new HashMap<>();
		this.componentIndexer = new ClassIndexer<>();
		this.selfInjectionDisabledOnAutoLoad = true;
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

	@Deprecated
	public ComponentMetadata loadComponent(Class<?> cls, boolean isWeak) {
		ComponentMetadata metadata = this.loadComponent(cls);

		if (!isWeak && metadata.getCreationResolveMode() != CreationResolveMode.DEFAULT) {
			metadata.setCreationResolveMode(CreationResolveMode.DEFAULT);
		}

		return metadata;
	}

	public ComponentMetadata loadComponent(Class<?> cls) {
		return this.loadComponentInternal(cls, false);
	}

	private ComponentMetadata loadComponentInternal(Class<?> cls, boolean disableSelfInjection) {
		if (!ComponentMetadata.isLoadable(cls)) {
			throw new CindyException("The " + cls + " is not loadable (is abstract or an interface)");
		}

		this.currentRecursionCallCount++;
		try {
			ComponentMetadata componentMetadata = this.getComponentMetadata(cls);

			if (componentMetadata == null) {
				componentMetadata = new ComponentMetadata(cls);
				this.metadatas.put(cls, componentMetadata);
				this.componentIndexer.add(componentMetadata, cls);

				this.log("Loaded component {#0}", cls.getSimpleName());
				for (ComponentDependency e : componentMetadata.getDependencies()) {
					if (ComponentMetadata.isLoadable(e.getComponentClass())) {
						if (e.isRequired() || !this.onlyChainLoadIfRequired) {
							this.loadComponentInternal(e.getComponentClass(), this.selfInjectionDisabledOnAutoLoad);
						}
					}
				}
			}

			Class<?> dependentClass = componentMetadata.getDependentClass();
			if (componentMetadata.isSingleton()) {
				if (this.application == null) {
					throw new CindyException("Unable to find application metadata.");
				}
				dependentClass = this.application.getClass();
			}
			if (dependentClass != null && !disableSelfInjection) {
				ComponentMetadata dependentClassMetadata = this.loadComponentInternal(dependentClass,
						this.selfInjectionDisabledOnAutoLoad);
				this.log("Added {#0} to dependency of {#1}", cls, dependentClassMetadata.getComponentClass());

				if (!dependentClassMetadata.hasDependency(cls)) {
					dependentClassMetadata.addDependency(cls, true, false, SearchScope.UNDEFINED, CreationScope.UNDEFINED);
				}
			}

			return componentMetadata;
		} finally {
			this.currentRecursionCallCount--;
		}
	}

	private void log(String format, Object... params) {
		StringBuilder sb = new StringBuilder();

		if (!this.application.isInitialized()) {
			for (int i = 0; i < this.currentRecursionCallCount; i++) {
				sb.append(' ');
			}
		}

		sb.append(format);

		LOGGER.trace(Strings.format(sb.toString(), params));
	}

	private void throwUnableToFindCandidate(List<ComponentMetadata> components, Class<?> objectClass) {
		List<ComponentMetadata> weakCandidates = components.stream().filter(e -> e.getCreationResolveMode() == CreationResolveMode.FALLBACK)
				.collect(Collectors.toList());
		List<ComponentMetadata> strongCandidates = components.stream().filter(e -> e.getCreationResolveMode() == CreationResolveMode.DEFAULT)
				.collect(Collectors.toList());

		DynamicText dt = new DynamicText(
				""
						+ "Too many component candidates found for {objectClass}\n"
						+ "Default candidates:\n"
						+ "[strongCandidates-> candidate:"
						+ "{candidate.componentClass}\n"
						+ "]\n"
						+ "Fallback candidates:\n"
						+ "[weakCandidates-> candidate:"
						+ "{candidate.componentClass}\n"
						+ "]"
		);

		dt.put("objectClass", objectClass);
		dt.put("weakCandidates", weakCandidates);
		dt.put("strongCandidates", strongCandidates);

		throw new CindyException(dt.toString());
	}

	public ComponentMetadata getCompatibleMetadata(Class<?> objectClass) {
		List<ComponentMetadata> compatibleComponents = this.findCompatibleComponentForClass(objectClass);

		if (compatibleComponents == null) {
			return null;
		}

		int weakCandidateCount = 0;
		ComponentMetadata fallbackCandidate = null;
		ComponentMetadata defaultCandidate = null;

		for (ComponentMetadata metadata : compatibleComponents) {
			if (metadata.getCreationResolveMode() == CreationResolveMode.FALLBACK) {
				fallbackCandidate = metadata;
				weakCandidateCount++;
			} else {
				if (defaultCandidate == null) {
					defaultCandidate = metadata;
				} else {
					this.throwUnableToFindCandidate(compatibleComponents, objectClass);
				}
			}
		}

		ComponentMetadata metadata = null;
		if (defaultCandidate == null) {
			if (weakCandidateCount != 1) {
				this.throwUnableToFindCandidate(compatibleComponents, objectClass);
			}
			metadata = fallbackCandidate;
		} else {
			metadata = defaultCandidate;
		}

		return metadata;
	}

	public void setFactory(Factory<?> factory, Class<?> managedClass) {
		this.loadComponent(managedClass).setFactory(factory);
	}

	public void signalComponentCreated(Object objectInstance) {
		IComponentFactoryListener listener = this.listener;
		if (listener != null) {
			listener.onComponentCreated(objectInstance);
		}
	}

	public void signalComponentInitialized(Object objectInstance) {
		IComponentFactoryListener listener = this.listener;
		if (listener != null) {
			listener.onComponentInitialized(objectInstance);
		}
	}

	public void signalComponentInitializationFailed(Object objectInstance, Exception e) {
		IComponentFactoryListener listener = this.listener;
		if (listener != null) {
			listener.onComponentInitializationFailed(objectInstance, e);
		}
	}

	public ComponentInitializer createInitializer() {
		return new ComponentInitializer(this);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Collection<ComponentMetadata> getLoadedComponentMetadatas() {
		return this.metadatas.values();
	}

	public IComponentFactoryListener getListener() {
		return this.listener;
	}

	public void setListener(IComponentFactoryListener listener) {
		this.listener = listener;
	}

	public CindyApp getApplication() {
		return this.application;
	}

	public void setApplication(CindyApp application) {
		this.application = application;
	}

	/**
	 * Defines whether the ComponentMetadataManager should auto load a dependency only when required or everytime
	 * a dependency is detected
	 *
	 * @return
	 */
	public boolean isOnlyChainLoadIfRequired() {
		return onlyChainLoadIfRequired;
	}

	public void setOnlyChainLoadIfRequired(boolean onlyChainLoadIfRequired) {
		this.onlyChainLoadIfRequired = onlyChainLoadIfRequired;
	}

	public boolean isSelfInjectionDisabledOnAutoLoad() {
		return selfInjectionDisabledOnAutoLoad;
	}

	public void setSelfInjectionDisabledOnAutoLoad(boolean selfInjectionDisabledOnAutoLoad) {
		this.selfInjectionDisabledOnAutoLoad = selfInjectionDisabledOnAutoLoad;
	}
}
