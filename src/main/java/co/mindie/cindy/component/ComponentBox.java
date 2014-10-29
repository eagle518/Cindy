/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// ComponentBox.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 10, 2014 at 6:05:55 PM
////////

package co.mindie.cindy.component;

import co.mindie.cindy.automapping.SearchScope;
import co.mindie.cindy.exception.CindyException;
import me.corsin.javatools.array.ArrayUtils;
import me.corsin.javatools.misc.NullArgumentException;
import me.corsin.javatools.misc.Pair;
import me.corsin.javatools.misc.ValueHolder;
import me.corsin.javatools.reflect.ClassIndexer;
import me.corsin.javatools.string.Strings;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class ComponentBox implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////

	private static AtomicLong ID_SEQUENCE = new AtomicLong();
	private ComponentBox superBox;
	private ClassIndexer<Object> indexer;
	private Set<Object> components;
	private List<Closeable> closeables;
	private List<ComponentBox> childComponentBoxes;
	private long id;
	private ComponentAspect[] neededAspects;
	private ComponentAspect[] rejectedAspects;
	private Object owner;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentBox() {
		this(new ComponentAspect[0], new ComponentAspect[0]);
	}

	public ComponentBox(ComponentAspect[] neededAspects, ComponentAspect[] rejectedAspects) {
		this(neededAspects, rejectedAspects, null);
	}

	public ComponentBox(ComponentAspect[] neededAspects, ComponentAspect[] rejectedAspects, ComponentBox superBox) {
		if (neededAspects == null) {
			throw new NullArgumentException("neededAspects");
		}

		if (rejectedAspects == null) {
			throw new NullArgumentException("rejectedAspects");
		}

		this.indexer = new ClassIndexer<>();
		this.components = new HashSet<>();
		this.closeables = new ArrayList<>();
		this.childComponentBoxes = new ArrayList<>();
		this.superBox = superBox;
		this.id = ID_SEQUENCE.getAndIncrement();
		this.neededAspects = neededAspects;
		this.rejectedAspects = rejectedAspects;
	}

	////////////////////////
	// METHODS
	////////////////

	public void addComponent(Object component, ComponentAspect[] aspects) {
		if (aspects == null) {
			throw new NullArgumentException("aspects");
		}

		if (!ArrayUtils.containsAll(this.neededAspects, aspects)) {
			throw new CindyException("Attempting to add a component {" + component + "} with aspects " + Strings.getObjectDescription(aspects) +
					" to a box that needs aspects " + Strings.getObjectDescription(this.neededAspects) + " (box owner: " + this.getOwner() + ")");
		}
		if (ArrayUtils.containsAny(this.rejectedAspects, aspects)) {
			throw new CindyException("Attempting to add a component {" + component + "} with aspects " + Strings.getObjectDescription(aspects) +
					" to a box that reject aspects " + Strings.getObjectDescription(this.rejectedAspects) + " (box owner: " + this.getOwner() + ")");
		}

		if (component instanceof Closeable && !this.closeables.contains(component)) {
			this.closeables.add((Closeable) component);
		}

		this.indexer.add(component, component.getClass());
		this.components.add(component);
	}

	public void removeComponent(Object component) {
		if (component instanceof Closeable) {
			this.removeCloseable((Closeable) component);
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

	public List<Object> findComponents(Class<?> accessibleClass, SearchScope searchScope, ValueHolder<ComponentBox> outputComponentBox) {
		if (searchScope == SearchScope.UNDEFINED) {
			throw new CindyException("Cannot find a component with a UNDEFINED searchScope");
		}

		List<Object> components = this.indexer.find(accessibleClass);

		if (outputComponentBox != null && components != null) {
			outputComponentBox.setValue(this);
		}

		if (this.superBox != null && searchScope == SearchScope.GLOBAL) {
			List<Object> parentComponents = this.superBox.findComponents(accessibleClass, searchScope, outputComponentBox);

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

	public Object findComponent(Class<?> accessibleClass, SearchScope searchScope, ValueHolder<ComponentBox> outputComponentContext) {
		List<Object> components = this.findComponents(accessibleClass, searchScope, outputComponentContext);

		if (components == null) {
			return null;
		}

		int size = components.size();

		if (size > 1) {
			StringBuilder sb = new StringBuilder();
			sb.append("Found too many components for ");
			sb.append(accessibleClass);
			sb.append(" using searchScope " + searchScope);
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

		for (Closeable closeable : this.closeables) {
			try {
				closeable.close();
			} catch (Exception e) {
				exceptions.add(new Pair<>(closeable, e));
			}
		}

		if (!exceptions.isEmpty()) {
			throw new CindyException("Failed to close component context.", exceptions.get(0).getSecond());
		}
	}

	public ComponentBox createChildBox() {
		return this.createChildBox(new ComponentAspect[0], new ComponentAspect[0], null);
	}

	/**
	 * Create a child ComponentBox that has this box as the super box.
	 * @param owner the owner of the new created ComponentBox
	 * @return the new created ComponentBox.
	 */
	public ComponentBox createChildBox(ComponentAspect[] neededAspects, ComponentAspect[] rejectedAspects, Object owner) {
		ComponentBox box = new ComponentBox(neededAspects, rejectedAspects, this);
		box.setOwner(owner);

		this.addChildBox(box);

		return box;
	}

	@Override
	public String toString() {
		return "(id=" + this.id +
				", owner=" + this.owner +
				")";
	}

	public void addChildBox(ComponentBox componentBox) {
		this.childComponentBoxes.add(componentBox);
		this.addCloseable(componentBox);
	}

	public void removeChildBox(ComponentBox componentBox) {
		this.childComponentBoxes.remove(componentBox);
		this.removeCloseable(componentBox);
	}

	private void addCloseable(Closeable closeable) {
		this.closeables.add(closeable);
	}

	private void removeCloseable(Closeable closeable) {
		this.closeables.remove(closeable);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public ComponentBox getSuperBox() {
		return this.superBox;
	}

	public void setSuperBox(ComponentBox superBox) {
		this.superBox = superBox;
	}

	public Set<Object> getComponents() {
		return this.components;
	}

	public List<Closeable> getCloseables() {
		return this.closeables;
	}

	public Object getOwner() {
		return this.owner;
	}

	public void setOwner(Object owner) {
		this.owner = owner;
	}

	public long getId() {
		return id;
	}

	public List<ComponentBox> getChildComponentBoxes() {
		return childComponentBoxes;
	}

	public ComponentAspect[] getNeededAspects() {
		return neededAspects;
	}

	public ComponentAspect[] getRejectedAspects() {
		return rejectedAspects;
	}
}