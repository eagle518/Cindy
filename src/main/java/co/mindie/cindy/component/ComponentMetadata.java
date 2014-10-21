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
	private Dependencies dependenciesAnnotation;
	private Box boxAnnotation;
	private Factory<Object> factory;
	private CreationResolveMode creationResolveMode;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentMetadata(Class<?> objectClass) {
		this.dependencies = new ArrayList<>();
		this.wires = new ArrayList<>();
		this.componentClass = objectClass;

		this.parseAnnotations();

		this.factory = () -> {
			try {
				return this.componentClass.newInstance();
			} catch (Exception e) {
				throw new CindyException("Unable to instantiate CindyComponent " + this.componentClass.getName(), e);
			}
		};

		if (this.dependenciesAnnotation != null) {
			for (Class<?> dependencyClass : this.dependenciesAnnotation.dependenciesClasses()) {
				this.addDependency(dependencyClass, true, false,
						SearchScope.UNDEFINED,
						CreationBox.CURRENT_BOX);
			}
		}

		this.load();
	}

	private void parseAnnotations() {
		Class<?> objectClass = this.componentClass;
		boolean foundComponentAnnotation = false;

		while ((!foundComponentAnnotation || this.dependenciesAnnotation == null || this.boxAnnotation == null) &&  objectClass != null) {
			if (!foundComponentAnnotation) {
				Component component = objectClass.getAnnotation(Component.class);
				if (component != null) {
					this.componentAnnotation = component;
					foundComponentAnnotation = true;
				} else {
					Singleton singleton = objectClass.getAnnotation(Singleton.class);
					if (singleton != null) {
						this.singletonAnnotation = singleton;
						foundComponentAnnotation = true;
					}
				}
			}

			if (this.dependenciesAnnotation == null) {
				this.dependenciesAnnotation = objectClass.getAnnotation(Dependencies.class);
			}

			if (this.boxAnnotation == null) {
				this.boxAnnotation = objectClass.getAnnotation(Box.class);
			}

			objectClass = objectClass.getSuperclass();
		}
	}

	private static <T extends Annotation> T getAnnotation(Class<?> objectClass, Class<T> annotationClass) {
		T annotation = null;

		while (annotation == null && objectClass != null) {
			annotation = objectClass.getAnnotation(annotationClass);
			objectClass = objectClass.getSuperclass();
		}

		return annotation;
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

	private void load() {
		Class<?> currentClass = this.componentClass;

		while (currentClass != null) {
			Field[] fields = currentClass.getDeclaredFields();

			for (Field field : fields) {
				Wired wired = field.getAnnotation(Wired.class);

				if (wired != null) {
					field.setAccessible(true);
					Box box = field.getAnnotation(Box.class);

					Wire wire = new Wire(field, box, wired);

					Class<?> wireClass = wire.getFieldType();
					ComponentDependency dependency = this.addDependency(wireClass, wire.isRequired(), wire.isList(),
							wired.searchScope(),
							wired.creationBox()
					);
					dependency.setWire(wire);

					this.wires.add(wire);
				}
			}

			currentClass = currentClass.getSuperclass();
		}
	}

	public void autowire(Object object, ComponentBox ctx) {
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

	public ComponentDependency addDependency(Class<?> dependencyClass, boolean required, boolean isList, SearchScope searchScope, CreationBox creationBox) {
		searchScope = this.resolveSearchScope(dependencyClass, searchScope);

		ComponentDependency dependency = new ComponentDependency(dependencyClass, isList, searchScope, creationBox);
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
		if (this.dependenciesAnnotation != null) {
			return this.dependenciesAnnotation.dependentClass() != void.class ? this.dependenciesAnnotation.dependentClass() : null;
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

		if (this.singletonAnnotation != null) {
			return this.singletonAnnotation.creationResolveMode();
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

	public ComponentAspect[] getAspects() {
		if (this.singletonAnnotation != null) {
			return new ComponentAspect[] { ComponentAspect.SINGLETON, ComponentAspect.THREAD_SAFE };
		}

		if (this.componentAnnotation != null) {
			return this.componentAnnotation.aspects();
		}

		return new ComponentAspect[0];
	}

	public Box getBox() {
		return this.boxAnnotation;
	}
}
