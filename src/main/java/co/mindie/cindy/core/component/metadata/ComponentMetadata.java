/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.component
// ComponentMetadata.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 12, 2014 at 3:03:11 PM
////////

package co.mindie.cindy.core.component.metadata;

import co.mindie.cindy.core.annotation.*;
import co.mindie.cindy.core.component.BoxOptions;
import co.mindie.cindy.core.component.Aspect;
import co.mindie.cindy.core.component.CreationBox;
import co.mindie.cindy.core.component.SearchScope;
import co.mindie.cindy.core.exception.CindyException;
import co.mindie.cindy.core.module.Module;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ComponentMetadata {

	////////////////////////
	// VARIABLES
	////////////////

	final private Module module;
	final private Class<?> componentClass;
	final private List<Wire> wires;
	final private List<Wire> wireCores;
	final private List<ComponentDependency> dependencies;
	final private List<ComponentDependency> listDependencies;
	final private Load loadAnnotation;
	final private Aspects aspectsAnnotation;
	final private Dependencies dependenciesAnnotation;
	final private Box boxAnnotation;
	final private Map<Class<?>, Annotation> annotations;
	final private Map<Class<?>, List<Method>> methodsByAnnotations;
	final private Map<Class<?>, List<Field>> fieldsByAnnotations;
	final private Map<String, Object> configurations;
	final private ComponentMetadata parentComponentMetadata;

	private Factory<Object> factory;
	private Aspect[] aspects;
	private int creationPriority;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentMetadata(Module module, Class<?> objectClass, ComponentMetadata parentComponentMetadata) {
		this.module = module;
		this.dependencies = new ArrayList<>();
		this.wires = new ArrayList<>();
		this.wireCores = new ArrayList<>();
		this.listDependencies = new ArrayList<>();
		this.componentClass = objectClass;
		this.annotations = new HashMap<>();
		this.methodsByAnnotations = new HashMap<>();
		this.fieldsByAnnotations = new HashMap<>();
		this.configurations = new HashMap<>();

		this.parentComponentMetadata = parentComponentMetadata;

		this.factory = new ClassFactory<>((Class<Object>)objectClass);

		this.loadAnnotations();

		this.loadAnnotation = this.getAnnotation(Load.class);
		this.boxAnnotation = this.getAnnotation(Box.class);
		this.dependenciesAnnotation = this.getAnnotation(Dependencies.class);
		this.aspectsAnnotation = this.getAnnotation(Aspects.class);

		this.loadFields();

		if (this.dependenciesAnnotation != null) {
			for (Class<?> dependencyClass : this.dependenciesAnnotation.dependenciesClasses()) {
				this.addDependency(dependencyClass, true, false,
						SearchScope.UNDEFINED,
						CreationBox.CURRENT_BOX);
			}
		}

		this.creationPriority = this.loadAnnotation != null ? this.loadAnnotation.creationPriority() : 0;

		if (this.aspectsAnnotation != null) {
			this.aspects = this.aspectsAnnotation.aspects();
		} else {
			this.aspects = new Aspect[0];
		}
	}

	////////////////////////
	// METHODS
	////////////////

	private SearchScope resolveSearchScope(Class<?> dependencyClass, SearchScope input) {
		if (input != SearchScope.UNDEFINED) {
			return input;
		}

		SearchScope searchScope = this.dependenciesAnnotation != null ? this.dependenciesAnnotation.searchScope() : SearchScope.GLOBAL;

		if (searchScope == SearchScope.UNDEFINED) {
			throw new CindyException("Unable to determine a SearchScope for " + dependencyClass + " in " + this.componentClass +
					" (Both the Component and the wire has an UNDEFINED SearchScope)");
		}

		return searchScope;
	}

	public static boolean isLoadable(Class<?> cls) {
		return (cls.getModifiers() & Modifier.ABSTRACT) == 0 && !cls.isInterface();
	}

	private void loadFields() {
		List<Field> wires = this.getFieldsWithAnnotation(Wired.class);

		if (wires != null) {
			for (Field field : wires) {
				Wired wired = field.getAnnotation(Wired.class);
				field.setAccessible(true);
				Box box = field.getAnnotation(Box.class);

				Wire wire = new Wire(field, box != null ? new BoxOptions(box.needAspects(), box.rejectAspects(), box.readOnly()) : null, null);

				Class<?> wireClass = wire.getFieldType();
				ComponentDependency dependency = this.addDependency(wireClass, wired.required(), wire.isList(),
						wired.searchScope(),
						wired.creationBox()
				);
				dependency.setWire(wire);

				Load load = field.getAnnotation(Load.class);

				if (load != null) {
					dependency.setLoadInstruction(new LoadInstruction(load.creationPriority()));
				}

				this.wires.add(wire);
			}
		}

		List<Field> cores = this.getFieldsWithAnnotation(Core.class);

		if (cores != null) {
			for (Field field : cores) {
				Core core = field.getAnnotation(Core.class);
				field.setAccessible(true);

				Wire wire = new Wire(field, null, core.value());
				this.wireCores.add(wire);
			}
		}

		List<Field> configs = this.getFieldsWithAnnotation(Config.class);
		if (configs != null) {
			for (Field field : cores) {
				field.setAccessible(true);
			}
		}
	}

	private <T> void storeAnnotation(Annotation annotation, Map<Class<?>, List<T>> map, T entry) {
		Class<?> annotationClass = annotation.annotationType();

		List<T> entries = map.get(annotationClass);
		if (entries == null) {
			entries = new ArrayList<>();
			map.put(annotationClass, entries);
		}

		entries.add(entry);
	}

	private void loadAnnotations() {
		Class<?> currentClass = this.componentClass;

		while (currentClass != null) {
			for (Annotation annotation : currentClass.getAnnotations()) {
				Class<?> annotationClass = annotation.annotationType();
				if (!this.annotations.containsKey(annotationClass)) {
					this.annotations.put(annotationClass, annotation);
				}
			}

			for (Method method : currentClass.getDeclaredMethods()) {
				for (Annotation annotation : method.getAnnotations()) {
					this.storeAnnotation(annotation, this.methodsByAnnotations, method);
				}
			}

			for (Field field : currentClass.getDeclaredFields()) {
				for (Annotation annotation : field.getAnnotations()) {
					this.storeAnnotation(annotation, this.fieldsByAnnotations, field);
				}
			}

			currentClass = currentClass.getSuperclass();
		}
	}

	public boolean hasDependency(Class<?> cls) {
		for (ComponentDependency dependency : this.dependencies) {
			if (!dependency.isList() && dependency.getComponentClass() == cls) {
				return true;
			}
		}

		return false;
	}

	public ComponentDependency addDependency(Class<?> dependencyClass, boolean required, boolean isList, SearchScope searchScope, CreationBox creationBox) {
		searchScope = this.resolveSearchScope(dependencyClass, searchScope);

		ComponentDependency dependency = new ComponentDependency(dependencyClass, isList, searchScope, creationBox);

		if (isList) {
			this.listDependencies.add(dependency);
		} else {
			this.dependencies.add(dependency);
		}

		dependency.setRequired(required);

		return dependency;
	}

	public Object createInstance() {
		Object instance = this.factory.createComponent();

		List<Field> configs = this.getFieldsWithAnnotation(Config.class);

		if (configs != null) {
			for (Field field : configs) {
				Config config = field.getAnnotation(Config.class);
				Object value = this.getConfig(config.key(), config.useModuleConf());
				try {
					field.set(instance, value);
				} catch (Exception e) {
					throw new CindyException("Unable to set config key " + config.key() + " to value " + value + " to field @Config " + field, e);
				}
			}
		}

		return instance;
	}

	/**
	 * @return The first found annotation of the given annotationClass in the Component Java Object hierarchy, or null if not found.
	 */
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return (T)this.annotations.get(annotationClass);
	}

	/**
	 * @return The list of methods that has the given annotationClass in the Component Java Object hierarchy, or null if not found.
	 */
	public <T extends Annotation> List<Method> getMethodsWithAnnotation(Class<T> annotationClass) {
		return this.methodsByAnnotations.get(annotationClass);
	}

	/**
	 * @return The list of fields that has the given annotaitonClass in the Component Object hierarchy, or null if not found.
	 */
	public <T extends Annotation> List<Field> getFieldsWithAnnotation(Class<T> annotationClass) {
		return this.fieldsByAnnotations.get(annotationClass);
	}

	public String getLoadDescriptionContext() {
		if (this.parentComponentMetadata == null) {
			if (this.module == null) {
				return this.componentClass + " was loaded manually using loadComponent()";
			} else {
				return this.componentClass + " was loaded by module " + this.module;
			}
		} else {
			return this.componentClass + " was loaded by " + this.parentComponentMetadata.getComponentClass() + "\n" +
					this.parentComponentMetadata.getLoadDescriptionContext();
		}
	}

	public void putConfig(String key, Object value) {
		this.configurations.put(key, value);
	}

	public Object getConfig(String key) {
		return this.getConfig(key, true);
	}

	public Object getConfig(String key, boolean useModuleConfig) {
		Object value = this.configurations.get(key);

		if (value == null && useModuleConfig && this.module != null) {
			value = this.module.getConfiguration().get(key);
		}

		return value;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Class<?> getComponentClass() {
		return this.componentClass;
	}

	public List<ComponentDependency> getDependencies() {
		return this.dependencies;
	}

	public Factory<?> getFactory() {
		return this.factory;
	}

	@SuppressWarnings("unchecked")
	public void setFactory(Factory<?> factory) {
		this.factory = (Factory<Object>) factory;
	}

	public Aspect[] getAspects() {
		return this.aspects;
	}

	public void setAspects(Aspect[] aspects) {
		this.aspects = aspects;
	}

	public Box getBox() {
		return this.boxAnnotation;
	}

	public Set<Class<?>> getAnnotationClasses() {
		return this.annotations.keySet();
	}

	public int getCreationPriority() {
		return creationPriority;
	}

	public void setCreationPriority(int creationPriority) {
		this.creationPriority = creationPriority;
	}

	public List<Wire> getWireCores() {
		return wireCores;
	}

	public List<ComponentDependency> getListDependencies() {
		return listDependencies;
	}

	public ComponentMetadata getParentComponentMetadata() {
		return parentComponentMetadata;
	}

	public Module getModule() {
		return module;
	}
}
