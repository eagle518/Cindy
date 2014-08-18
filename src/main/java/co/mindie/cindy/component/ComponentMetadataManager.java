/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// InstanceTrigger.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 10, 2014 at 2:38:28 PM
////////

package co.mindie.cindy.component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import co.mindie.cindy.CindyApp;
import co.mindie.cindy.automapping.CreationScope;
import co.mindie.cindy.automapping.SearchScope;
import co.mindie.cindy.exception.CindyException;
import me.corsin.javatools.dynamictext.DynamicText;
import me.corsin.javatools.reflect.ClassIndexer;
import me.corsin.javatools.string.Strings;

import org.apache.log4j.Logger;

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

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentMetadataManager() {
		this.metadatas = new HashMap<>();
		this.componentIndexer = new ClassIndexer<>();
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

	public ComponentMetadata loadComponent(Class<?> cls) {
		return this.loadComponent(cls, false);
	}

	public ComponentMetadata loadComponent(Class<?> cls, boolean isWeak) {
		if (!ComponentMetadata.isLoadable(cls)) {
			throw new CindyException("The " + cls + " is not loadable (is abstract or an interface)");
		}

		this.currentRecursionCallCount++;
		try {
			ComponentMetadata componentMetadata = this.getComponentMetadata(cls);
			boolean created = false;

			if (componentMetadata == null) {
				componentMetadata = new ComponentMetadata(cls);
				this.metadatas.put(cls, componentMetadata);
				this.componentIndexer.add(componentMetadata, cls);

				created = true;
				this.log("Loaded {#0} component {#1}", isWeak ? "weak" : "strong", cls.getSimpleName());
				for (ComponentDependency e : componentMetadata.getDependencies()) {
					if (ComponentMetadata.isLoadable(e.getComponentClass())) {
						this.loadComponent(e.getComponentClass(), true);
					}
				}
			}

			if (!isWeak && componentMetadata.getDependentClass() != null) {
				ComponentMetadata dependentClassMetadata = this.loadComponent(componentMetadata.getDependentClass(), true);
				this.log("Added {#0} to dependency of {#1}", cls, dependentClassMetadata.getComponentClass());

				if (!dependentClassMetadata.hasDependency(cls)) {
					dependentClassMetadata.addDependency(cls, true, false, SearchScope.UNDEFINED, CreationScope.UNDEFINED);
				}
			}

			if (!isWeak && componentMetadata.isWeak()) {
				componentMetadata.setWeak(false);

				if (!created) {
					this.log("{#0} became strong component", cls.getSimpleName());
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
		List<ComponentMetadata> weakCandidates = components.stream().filter(e -> e.isWeak()).collect(Collectors.toList());
		List<ComponentMetadata> strongCandidates = components.stream().filter(e -> !e.isWeak()).collect(Collectors.toList());

		DynamicText dt = new DynamicText(
				""
						+ "Too many component candidates found for {objectClass}\n"
						+ "Strong candidates:\n"
						+ "[strongCandidates-> candidate:"
						+ "{candidate.componentClass}\n"
						+ "]\n"
						+ "Weak candidates:\n"
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
		ComponentMetadata weakCandidate = null;
		ComponentMetadata strongCandidate = null;

		for (ComponentMetadata metadata : compatibleComponents) {
			if (metadata.isWeak()) {
				weakCandidate = metadata;
				weakCandidateCount++;
			} else {
				if (strongCandidate == null) {
					strongCandidate = metadata;
				} else {
					this.throwUnableToFindCandidate(compatibleComponents, objectClass);
				}
			}
		}

		ComponentMetadata metadata = null;
		if (strongCandidate == null) {
			if (weakCandidateCount != 1) {
				this.throwUnableToFindCandidate(compatibleComponents, objectClass);
			}
			metadata = weakCandidate;
		} else {
			metadata = strongCandidate;
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
}
