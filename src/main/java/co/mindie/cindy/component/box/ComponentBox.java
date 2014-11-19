/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// ComponentBox.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 10, 2014 at 6:05:55 PM
////////

package co.mindie.cindy.component.box;

import co.mindie.cindy.automapping.SearchScope;
import co.mindie.cindy.component.ComponentAspect;
import co.mindie.cindy.exception.CindyException;
import co.mindie.cindy.utils.Initializable;
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

public abstract class ComponentBox implements Closeable, Initializable {

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

	public <T> List<T> findComponents(Class<T> accessibleClass) {
		return this.findComponents(accessibleClass, SearchScope.GLOBAL, null);
	}

	public <T> List<T> findComponents(Class<T> accessibleClass, SearchScope searchScope) {
		return this.findComponents(accessibleClass, searchScope, null);
	}

	public <T> List<T> findComponents(Class<T> accessibleClass, SearchScope searchScope, List<ComponentBox> outputComponentBoxes) {
		if (searchScope == SearchScope.UNDEFINED) {
			throw new CindyException("Cannot find a component with a UNDEFINED searchScope");
		}

		List<T> components = (List<T>)this.indexer.find(accessibleClass);

		if (outputComponentBoxes != null && components != null) {
			components.forEach(e -> outputComponentBoxes.add(this));
		}

		if (this.superBox != null && searchScope == SearchScope.GLOBAL) {
			List<T> parentComponents = this.superBox.findComponents(accessibleClass, searchScope, outputComponentBoxes);

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

	public <T> T findComponent(Class<T> accessibleClass) {
		return this.findComponent(accessibleClass, SearchScope.GLOBAL);
	}

	public <T> T findComponent(Class<T> accessibleClass, SearchScope searchScope) {
		return this.findComponent(accessibleClass, searchScope, null);
	}

	public <T> T findComponent(Class<T> accessibleClass, SearchScope searchScope, ValueHolder<ComponentBox> outputComponentBox) {
		List<ComponentBox> outputBoxes = new ArrayList<>();
		List<T> components = this.findComponents(accessibleClass, searchScope, outputBoxes);

		if (components == null) {
			return null;
		}

		int size = components.size();

		if (size > 1) {
			StringBuilder sb = new StringBuilder();
			sb.append("Found too many components for ");
			sb.append(accessibleClass);
			sb.append(" using searchScope ").append(searchScope);
			sb.append(" in ").append(this);
			sb.append("\nOthers are:\n");
			int i = 0;
			for (Object component : components) {
				sb.append(component.getClass())
						.append(" in ComponentBox ")
						.append(outputBoxes.get(i))
						.append(": ")
						.append(component).append("\n");
				i++;
			}

			throw new CindyException(sb.toString());
		}

		if (size == 1) {
			if (outputComponentBox != null) {
				outputComponentBox.setValue(outputBoxes.get(0));
			}

			return components.get(0);
		} else if (outputComponentBox != null) {
			outputComponentBox.setValue(outputBoxes.get(0));
		}

		return null;
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

		for (ComponentBox childBox : this.childComponentBoxes) {
			try {
				childBox.close();
			} catch (Exception e) {
				exceptions.add(new Pair<>(childBox, e));
			}
		}

		if (!exceptions.isEmpty()) {
			throw new CindyException("Failed to close component context.", exceptions.get(0).getSecond());
		}
	}

	public ComponentBox createChildBox(boolean readOnly, Object owner) {
		return this.createChildBox(new ComponentAspect[0], new ComponentAspect[0], owner, readOnly);
	}

	public static ComponentBox create(boolean readOnly) {
		return create(new ComponentAspect[0], new ComponentAspect[0], null, readOnly);
	}

	public static ComponentBox create(ComponentAspect[] neededAspects, ComponentAspect[] rejectedAspects, ComponentBox superBox, boolean readOnly) {
		if (readOnly) {
			return new ReadOnlyComponentBox(neededAspects, rejectedAspects, superBox);
		} else {
			return new ThreadSafeComponentBox(neededAspects, rejectedAspects, superBox);
		}
	}

	/**
	 * Create a child ComponentBox that has this box as the super box.
	 *
	 * @param owner the owner of the new created ComponentBox
	 * @return the new created ComponentBox.
	 */
	public ComponentBox createChildBox(ComponentAspect[] neededAspects, ComponentAspect[] rejectedAspects, Object owner, boolean readOnly) {
		ComponentBox box = create(neededAspects, rejectedAspects, this, readOnly);
		box.setOwner(owner);

		this.addChildBox(box);

		return box;
	}

	@Override
	public String toString() {
		return this.toString(false);
	}

	public String toString(boolean showContents) {
		if (showContents) {
			StringBuilder children = new StringBuilder();
			this.getChildComponentBoxes().forEach(child -> children.append(child.toString(true)));

			return "(id=" + this.id +
					", owner=" + this.owner +
					", components=" + this.getComponents() +
					", childBoxes= " + children.toString()
					+ ")";
		} else {
			return "(id=" + this.id +
					", owner=" + this.owner +
					")";
		}
	}

	public void addChildBox(ComponentBox componentBox) {
		this.childComponentBoxes.add(componentBox);
	}

	public void removeChildBox(ComponentBox componentBox) {
		this.childComponentBoxes.remove(componentBox);
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
