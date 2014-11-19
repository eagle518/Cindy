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
import co.mindie.cindy.automapping.MetadataModifier;
import co.mindie.cindy.exception.CindyException;
import co.mindie.cindy.misc.ComponentScanner;
import me.corsin.javatools.reflect.ClassIndexer;
import me.corsin.javatools.string.Strings;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ComponentMetadataManagerBuilder extends ComponentMetadatasHolder {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(ComponentMetadataManagerBuilder.class);

	private Map<Object, Object> infos;
	private Set<Method> metadataModifiers;
	private Deque<Method> metadataModifiersQueue;
	private int currentRecursionCallCount;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentMetadataManagerBuilder() {
		super(new HashMap<>(), new ClassIndexer<>(), new HashMap<>());

		this.metadataModifiers = new HashSet<>();
		this.metadataModifiersQueue = new ArrayDeque<>();
		this.infos = new HashMap<>();
	}

	////////////////////////
	// METHODS
	////////////////

	public List<ComponentMetadata> loadComponents(String classPath) {
		ComponentScanner scanner = new ComponentScanner(classPath);

		final List<ComponentMetadata> metadatas = new ArrayList<>();

		for (Class<?> type : scanner.findAnnotedTypes(Load.class)) {
			metadatas.add(this.loadComponent(type));
		}

		return metadatas;
 	}

	public ComponentMetadataManager build() {
		while (!this.metadataModifiersQueue.isEmpty()) {
			Method method = this.metadataModifiersQueue.poll();
			try {
				method.invoke(null, this);
			} catch (InvocationTargetException e) {
				throw new CindyException("Unable to run MetadataModifier", e.getTargetException());
			} catch (Exception e) {
				throw new CindyException("Unable to run MetadataModifier", e);
			}
		}

		return new ComponentMetadataManager(this.metadatas, this.componentIndexer, this.metadatasByAnnotation);
	}

	public ComponentMetadata loadComponent(Class<?> cls) {
		return this.loadComponent(cls, null);
	}

	private ComponentMetadata loadComponent(Class<?> cls, ComponentMetadata parentComponentMetadata) {
		this.currentRecursionCallCount++;
		try {
			ComponentMetadata componentMetadata = this.getComponentMetadata(cls);

			if (componentMetadata == null) {
				componentMetadata = new ComponentMetadata(cls, parentComponentMetadata);
				this.metadatas.put(cls, componentMetadata);
				this.componentIndexer.add(componentMetadata, cls);

				List<Method> methodModifiers = componentMetadata.getMethodsWithAnnotation(MetadataModifier.class);
				if (methodModifiers != null) {
					for (Method method : methodModifiers) {
						if (!Modifier.isStatic(method.getModifiers()) || method.getParameterCount() != 1 || method.getParameters()[0].getType() != ComponentMetadataManagerBuilder.class) {
							throw new CindyException("Methods with @MetadataModifier must be static and takes exactly ONE parameter of type ComponentMetadataManagerBuilder");
						}
						if (!this.metadataModifiers.contains(method)) {
							this.metadataModifiers.add(method);
							this.metadataModifiersQueue.add(method);
						}
					}
				}

				this.log("Loaded component {#0}", cls.getSimpleName());
				for (ComponentDependency e : componentMetadata.getDependencies()) {
					if (e.getComponentClass().getAnnotation(Load.class) != null) {
						this.loadComponent(e.getComponentClass(), componentMetadata);
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

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Collection<ComponentMetadata> getLoadedComponentMetadatas() {
		return this.metadatas.values();
	}

}
