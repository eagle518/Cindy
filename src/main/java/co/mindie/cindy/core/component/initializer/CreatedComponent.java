package co.mindie.cindy.core.component.initializer;

import co.mindie.cindy.core.component.box.ComponentBox;
import co.mindie.cindy.core.component.metadata.ComponentMetadata;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

public class CreatedComponent<T> implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////

	private T instance;
	private ComponentMetadata metadata;
	private ComponentBox enclosingBox;
	private ComponentBox innerBox;
	private Class<T> cls;
	private List<Object> dependencies;
	private boolean initialized;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CreatedComponent(T instance, ComponentMetadata metadata, ComponentBox enclosingBox, ComponentBox innerBox, Class<T> cls) {
		this.instance = instance;
		this.metadata = metadata;
		this.enclosingBox = enclosingBox;
		this.innerBox = innerBox;
		this.cls = cls;
		this.dependencies = new ArrayList<>();
	}

	////////////////////////
	// METHODS
	////////////////

	public void addDependency(Object dependencies) {
		this.dependencies.add(dependencies);
	}

	@Override
	public void close() {
		if (this.innerBox != null) {
			this.innerBox.close();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public T getInstance() {
		return this.instance;
	}

	public ComponentMetadata getMetadata() {
		return this.metadata;
	}

	public ComponentBox getEnclosingBox() {
		return this.enclosingBox;
	}

	public Class<T> getCls() {
		return this.cls;
	}

	public List<Object> getDependencies() {
		return this.dependencies;
	}

	public boolean isInitialized() {
		return this.initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public ComponentBox getInnerBox() {
		return innerBox;
	}

	public void setInnerBox(ComponentBox innerBox) {
		this.innerBox = innerBox;
	}

	public ComponentBox getCurrentBox() {
		return this.innerBox != null ? this.innerBox : this.enclosingBox;

	}
}
