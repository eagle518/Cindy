/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.component
// ComponentMetadata.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 12, 2014 at 3:03:11 PM
////////

package co.mindie.cindy.component;

import co.mindie.cindy.automapping.Component;
import co.mindie.cindy.automapping.CreationResolveMode;
import co.mindie.cindy.automapping.CreationScope;
import co.mindie.cindy.automapping.SearchScope;
import co.mindie.cindy.automapping.Singleton;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.exception.CindyException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ComponentMetadata {

	////////////////////////
	// VARIABLES
	////////////////

	private Class<?> componentClass;
	private List<Wire> wires;
	private List<ComponentDependency> dependencies;
	private Component componentAnnotation;
	private Singleton singletonAnnotation;
	private Factory<Object> factory;
	private boolean isWeak;
	private CreationResolveMode creationResolveMode;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentMetadata(Class<?> objectClass) {
		this.isWeak = true;
		this.dependencies = new ArrayList<>();
		this.wires = new ArrayList<>();
		this.componentClass = objectClass;
		this.componentAnnotation = objectClass.getAnnotation(Component.class);
		this.singletonAnnotation = objectClass.getAnnotation(Singleton.class);

		this.factory = () -> {
			try {
				return this.componentClass.newInstance();
			} catch (Exception e) {
				throw new CindyException("Unable to instantiate CindyComponent " + this.componentClass.getName(), e);
			}
		};

		if (this.componentAnnotation != null) {
			for (Class<?> dependencyClass : this.componentAnnotation.dependenciesClasses()) {
				this.addDependency(dependencyClass, true, false,
						SearchScope.UNDEFINED,
						CreationScope.UNDEFINED);
			}
		}

		if (this.singletonAnnotation != null && this.componentAnnotation == null) {
			this.creationResolveMode = CreationResolveMode.DEFAULT;
		}

		this.load();
	}

	////////////////////////
	// METHODS
	////////////////

	private SearchScope resolveSearchScope(Class<?> dependencyClass, SearchScope input) {
		if (input != SearchScope.UNDEFINED) {
			return input;
		}

		SearchScope searchScope = this.componentAnnotation != null ? this.componentAnnotation.dependenciesSearchScope() : SearchScope.GLOBAL;

		if (searchScope == SearchScope.UNDEFINED) {
			throw new CindyException("Unable to determine a SearchScope for " + dependencyClass + " in " + this.componentClass +
					" (Both the Component and the wire has an UNDEFINED SearchScope)");
		}

		return searchScope;
	}

	private CreationScope resolveCreationScope(Class<?> dependencyClass, CreationScope input) {
		if (input != CreationScope.UNDEFINED) {
			return input;
		}

		CreationScope creationScope = this.componentAnnotation != null ? this.componentAnnotation.dependenciesCreationScope() : CreationScope.LOCAL;

		if (creationScope == CreationScope.UNDEFINED) {
			throw new CindyException("Unable to determine a CreationScope for " + dependencyClass + " in " + this.componentClass +
					" (Both the Component and the wire has an UNDEFINED CreationScope)");
		}
		return creationScope;
	}

	public static boolean isLoadable(Class<?> cls) {
		return (cls.getModifiers() & Modifier.ABSTRACT) == 0 && !cls.isInterface();
	}

	private void load() {
		Class<?> currentClass = this.componentClass;

		while (currentClass != CindyComponent.class && currentClass != null) {
			Field[] fields = currentClass.getDeclaredFields();

			for (Field field : fields) {
				Wired wired = field.getAnnotation(Wired.class);

				if (wired != null) {
					field.setAccessible(true);

					Wire wire = new Wire(field, wired);

					Class<?> wireClass = wire.getFieldType();
					ComponentDependency dependency = this.addDependency(wireClass, wire.isRequired(), wire.isList(),
							wired.searchScope(),
							wired.creationScope()
					);
					dependency.setWire(wire);

					this.wires.add(wire);
				}
			}

			currentClass = currentClass.getSuperclass();
		}
	}

	public void autowire(Object object, ComponentContext ctx) {
		if (this.wires != null) {
			for (Wire wire : this.wires) {
				if (wire.getScope() != SearchScope.NO_SEARCH) {
					List<Object> components = null;
					Object component = null;

					if (ctx != null) {
						components = ctx.findComponents(wire.getFieldType(), wire.getScope());

						if (wire.isList()) {
							if (components == null) {
								component = new ArrayList<>();
							} else {
								component = new ArrayList<>(components);
							}
						} else {
							if (components != null) {
								if (components.size() > 1) {
									throw new CindyException("The class " + wire.getFieldType().getName() + " has more than one instance in this context");
								}
								component = components.get(0);
							}
						}
					}

					wire.set(object, component);
				}
			}
		}
	}

	public void wire(Object object, Object otherComponent) {
		if (otherComponent != null) {
			if (this.wires != null) {
				for (Wire wire : this.wires) {
					Class<?> componentClass = otherComponent.getClass();
					if (wire.canSet(componentClass)) {
						wire.set(object, otherComponent);
					}
				}
			}
		}
	}

	public void unwire(Object component, Object otherComponent) {
		if (otherComponent != null) {
			if (this.wires != null) {
				for (Wire wire : this.wires) {
					if (wire.get(component) == otherComponent) {
						wire.set(component, null);
					}
				}
			}
		}
	}

	public boolean hasDependency(Class<?> cls) {
		for (ComponentDependency dependency : this.dependencies) {
			if (dependency.getComponentClass() == cls) {
				return true;
			}
		}

		return false;
	}

	public ComponentDependency addDependency(Class<?> dependencyClass, boolean required, boolean isList, SearchScope searchScope, CreationScope creationScope) {
		searchScope = this.resolveSearchScope(dependencyClass, searchScope);
		creationScope = this.resolveCreationScope(dependencyClass, creationScope);

		ComponentDependency dependency = new ComponentDependency(dependencyClass, isList, searchScope, creationScope);
		this.dependencies.add(dependency);

		dependency.setRequired(required);

		return dependency;
	}

	public Object createInstance() {
		return this.factory.createComponent();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Class<?> getComponentClass() {
		return this.componentClass;
	}

	public Class<?> getDependentClass() {
		if (this.componentAnnotation != null) {
			return this.componentAnnotation.dependentClass() != void.class ? this.componentAnnotation.dependentClass() : null;
		}
		return null;
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

	public CreationResolveMode getCreationResolveMode() {
		if (this.creationResolveMode != null) {
			return this.creationResolveMode;
		}

		if (this.componentAnnotation != null) {
			return this.componentAnnotation.creationResolveMode();
		}

		return CreationResolveMode.FALLBACK;
	}

	public void setCreationResolveMode(CreationResolveMode creationResolveMode) {
		this.creationResolveMode = creationResolveMode;
	}

	public boolean isSingleton() {
		return this.singletonAnnotation != null;
	}
}
