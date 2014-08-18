/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// ComponentContext.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 10, 2014 at 6:05:55 PM
////////

package co.mindie.cindy.component;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.mindie.cindy.exception.CindyException;
import me.corsin.javatools.misc.Pair;
import me.corsin.javatools.misc.ValueHolder;
import me.corsin.javatools.reflect.ClassIndexer;
import co.mindie.cindy.automapping.SearchScope;

public class ComponentContext implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////

//	private static final Logger LOGGER = Logger.getLogger(ComponentContext.class);
	private ComponentContext parentContext;
	private ClassIndexer<Object> indexer;
	private Set<Object> components;
	private Set<Closeable> closeables;
	private Object owner;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentContext() {
		this(null);
	}

	public ComponentContext(ComponentContext parentContext) {
		this.indexer = new ClassIndexer<>();
		this.components = new HashSet<>();
		this.closeables = new HashSet<>();
		this.parentContext = parentContext;
	}

	////////////////////////
	// METHODS
	////////////////

	public void addComponent(Object component) {
		if (this.owner == null) {
			this.owner = component;
		}

		if (component instanceof CindyComponent) {
			CindyComponent cindyComponent = (CindyComponent) component;
			cindyComponent.setComponentContext(this);
		}

		if (component instanceof Closeable) {
			this.closeables.add((Closeable) component);
		}

		this.indexer.add(component, component.getClass());
		this.components.add(component);
	}

	public void removeComponent(Object component) {
		if (this.owner == component) {
			this.owner = null;
		}

		if (component instanceof CindyComponent) {
			CindyComponent cindyComponent = (CindyComponent) component;
			cindyComponent.setComponentContext(null);
		}

		if (component instanceof Closeable) {
			this.removeCloseable((Closeable)component);
		}

		this.indexer.remove(component, component.getClass());
		this.components.remove(component);
	}

	public List<Object> findComponents(Class<?> accessibleClass) {
		return this.findComponents(accessibleClass, SearchScope.GLOBAL, null);
	}

	public List<Object> findComponents(Class<?> accessibleClass, SearchScope searchScope) {
		return this.findComponents(accessibleClass, searchScope, null);
	}

	public List<Object> findComponents(Class<?> accessibleClass, SearchScope searchScope, ValueHolder<ComponentContext> outputComponentContext) {
		if (searchScope == SearchScope.UNDEFINED) {
			throw new CindyException("Cannot find a component with a UNDEFINED searchScope");
		}

		List<Object> components = this.indexer.find(accessibleClass);

		if (outputComponentContext != null && components != null) {
			outputComponentContext.setValue(this);
		}

		if (this.parentContext != null && searchScope == SearchScope.GLOBAL) {
			List<Object> parentComponents = this.parentContext.findComponents(accessibleClass, searchScope, outputComponentContext);

			if (components == null) {
				return parentComponents;
			} else {
				if (parentComponents != null) {
					components.addAll(parentComponents);
				}
			}
		}

		return components;
	}

	public Object findComponent(Class<?> accessibleClass) {
		return this.findComponent(accessibleClass, SearchScope.GLOBAL);
	}
	public Object findComponent(Class<?> accessibleClass, SearchScope searchScope) {
		return this.findComponent(accessibleClass, searchScope, null);
	}

	public Object findComponent(Class<?> accessibleClass, SearchScope searchScope, ValueHolder<ComponentContext> outputComponentContext) {
		List<Object> components = this.findComponents(accessibleClass, searchScope, outputComponentContext);

		if (components == null) {
			return null;
		}

		int size = components.size();

		if (size > 1) {
			StringBuilder sb = new StringBuilder();
			sb.append("Found too many components for ");
			sb.append(accessibleClass);
			sb.append(" in ").append(this);
			sb.append("\nOthers are:\n");
			for (Object component : components) {
				sb.append(component.getClass()).append(": ").append(component).append("\n");
			}

			throw new CindyException(sb.toString());
		}

		return size == 1 ? components.get(0) : null;
	}

	@Override
	public void close() {
		List<Pair<Object, Exception>> exceptions = new ArrayList<>();

		this.closeables.forEach((closeable) -> {
			try {
				closeable.close();
			} catch (Exception e) {
				exceptions.add(new Pair<>(closeable, e));
			}
		});

		if (!exceptions.isEmpty()) {
			throw new CindyException("Failed to close component context.", exceptions.get(0).getSecond());
		}
	}

	public ComponentContext createSubComponentContext() {
		return new ComponentContext(this);
	}

	@Override
	public String toString() {
		return "ComponentContext{" +
				"owner=" + this.owner +
				'}';
	}

	public void addCloseable(Closeable closeable) {
		this.closeables.add(closeable);
	}

	public void removeCloseable(Closeable closeable) {
		this.closeables.remove(closeable);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public ComponentContext getParentContext() {
		return this.parentContext;
	}

	public void setParentContext(ComponentContext parentContext) {
		this.parentContext = parentContext;
	}

	public Set<Object> getComponents() {
		return this.components;
	}

	public Object getOwner() {
		return this.owner;
	}

	public void setOwner(Object owner) {
		this.owner = owner;
	}
}
