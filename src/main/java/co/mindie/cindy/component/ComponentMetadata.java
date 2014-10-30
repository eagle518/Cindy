/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.component
// ComponentMetadata.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 12, 2014 at 3:03:11 PM
////////

package co.mindie.cindy.component;

import co.mindie.cindy.automapping.*;
import co.mindie.cindy.automapping.CreationBox;
import co.mindie.cindy.exception.CindyException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ComponentMetadata {

	////////////////////////
	// VARIABLES
	////////////////

	final private ComponentMetadataManager manager;
	final private Class<?> componentClass;
	final private List<Wire> wires;
	final private List<Wire> wireCores;
	final private List<ComponentDependency> dependencies;
	final private List<ComponentDependency> listDependencies;
	final private Load loadAnnotation;
	final private Component componentAnnotation;
	final private Dependencies dependenciesAnnotation;
	final private Box boxAnnotation;
	final private Map<Class<?>, Annotation> annotations;

	private Factory<Object> factory;
	private ComponentAspect[] aspects;
	private int creationPriority;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentMetadata(ComponentMetadataManager manager, Class<?> objectClass) {
		this.manager = manager;
		this.dependencies = new ArrayList<>();
		this.wires = new ArrayList<>();
		this.wireCores = new ArrayList<>();
		this.listDependencies = new ArrayList<>();
		this.componentClass = objectClass;
		this.annotations = new HashMap<>();

		this.factory = () -> {
			try {
				return this.componentClass.newInstance();
			} catch (Exception e) {
				throw new CindyException("Unable to instantiate CindyComponent " + this.componentClass.getName(), e);
			}
		};

		this.loadAnnotations();

		this.loadAnnotation = this.getAnnotation(Load.class);
		this.boxAnnotation = this.getAnnotation(Box.class);
		this.dependenciesAnnotation = this.getAnnotation(Dependencies.class);
		this.componentAnnotation = this.getAnnotation(Component.class);

		if (this.dependenciesAnnotation != null) {
			for (Class<?> dependencyClass : this.dependenciesAnnotation.dependenciesClasses()) {
				this.addDependency(dependencyClass, true, false,
						SearchScope.UNDEFINED,
						CreationBox.CURRENT_BOX);
			}
		}

		this.creationPriority = this.loadAnnotation != null ? this.loadAnnotation.creationPriority() : 0;

		if (this.componentAnnotation != null) {
			this.aspects = this.componentAnnotation.aspects();
		} else {
			this.aspects = new ComponentAspect[0];
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

	private void loadAnnotations() {
		Class<?> currentClass = this.componentClass;

		while (currentClass != null) {
			for (Annotation annotation : currentClass.getAnnotations()) {
				Class<?> annotationClass = annotation.annotationType();
				if (!this.annotations.containsKey(annotationClass)) {
					this.annotations.put(annotationClass, annotation);
				}
			}

			Field[] fields = currentClass.getDeclaredFields();

			for (Field field : fields) {
				Wired wired = field.getAnnotation(Wired.class);
				WiredCore wiredCore = field.getAnnotation(WiredCore.class);

				if (wired != null) {
					field.setAccessible(true);
					Box box = field.getAnnotation(Box.class);

					Wire wire = new Wire(field, box, null);

					Class<?> wireClass = wire.getFieldType();
					ComponentDependency dependency = this.addDependency(wireClass, wired.required(), wire.isList(),
							wired.searchScope(),
							wired.creationBox()
					);
					dependency.setWire(wire);

					this.wires.add(wire);
				}

				if (wiredCore != null) {
					field.setAccessible(true);

					Wire wire = new Wire(field, null, wiredCore.value());
					this.wireCores.add(wire);
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
		return this.factory.createComponent();
	}

	/**
	 * @return The first found annotation of the given annotationClass in the Component Java Object hierarchy, or null if not found.
	 */
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return (T)this.annotations.get(annotationClass);
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

	public ComponentAspect[] getAspects() {
		return this.aspects;
	}

	public void setAspects(ComponentAspect[] aspects) {
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

	public ComponentMetadataManager getManager() {
		return manager;
	}

	public List<Wire> getWireCores() {
		return wireCores;
	}

	public List<ComponentDependency> getListDependencies() {
		return listDependencies;
	}
}
