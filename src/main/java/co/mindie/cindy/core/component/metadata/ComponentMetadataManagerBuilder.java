/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// InstanceTrigger.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 10, 2014 at 2:38:28 PM
////////

package co.mindie.cindy.core.component.metadata;

import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.core.annotation.MetadataModifier;
import co.mindie.cindy.core.exception.CindyException;
import co.mindie.cindy.core.module.Module;
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
		super(new ArrayList<>(), new HashMap<>(), new ClassIndexer<>(), new HashMap<>());

		this.metadataModifiers = new HashSet<>();
		this.metadataModifiersQueue = new ArrayDeque<>();
		this.infos = new HashMap<>();
	}

	////////////////////////
	// METHODS
	////////////////

	public List<ComponentMetadata> loadModule(Module module) {
		List<ComponentMetadata> metadataList = new ArrayList<>();


		Module[] dependencies = module.getDependencies();

		if (dependencies != null) {
			for (Module dependency : dependencies){
				metadataList.addAll(this.loadModule(dependency));
			}
		}

		Class<?>[] componentClasses = module.getComponentClasses();
		String[] componentClasspaths = module.getComponentsClasspaths();

		if (componentClasses != null) {
			for (Class<?> cls : componentClasses) {
				metadataList.add(this.loadComponent(module, cls, null));
			}
		}

		if (componentClasspaths != null) {
			for (String classpath : componentClasspaths) {
				metadataList.addAll(this.loadComponents(module, classpath));
			}
		}

		this.getLoadedModules().add(module);

		return metadataList;
	}

	public List<ComponentMetadata> loadComponents(String classPath) {
		return this.loadComponents(null, classPath);
	}

	private List<ComponentMetadata> loadComponents(Module module, String classPath) {
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

		return new ComponentMetadataManager(this.loadedModules, this.metadatas, this.componentIndexer, this.metadatasByAnnotation);
	}

	public ComponentMetadata loadComponent(Class<?> cls) {
		return this.loadComponent(null, cls, null);
	}

	private ComponentMetadata loadComponent(Module module, Class<?> cls, ComponentMetadata parentComponentMetadata) {
		this.currentRecursionCallCount++;
		try {
			ComponentMetadata componentMetadata = this.getComponentMetadata(cls);

			if (componentMetadata == null) {
				componentMetadata = new ComponentMetadata(module, cls, parentComponentMetadata);
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
					if (e.getComponentClass().getAnnotation(Load.class) != null || e.getLoadInstruction() != null) {
						ComponentMetadata dependencyMetadata = this.loadComponent(module, e.getComponentClass(), componentMetadata);
						if (e.getLoadInstruction() != null) {
							dependencyMetadata.setCreationPriority(e.getLoadInstruction().getCreationPriority());
						}
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
