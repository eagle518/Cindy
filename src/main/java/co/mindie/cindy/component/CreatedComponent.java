/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.component
// CreatedComponent.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 7, 2014 at 12:07:19 PM
////////

package co.mindie.cindy.component;

import java.util.ArrayList;
import java.util.List;

public class CreatedComponent {

	////////////////////////
	// VARIABLES
	////////////////

	private Object instance;
	private ComponentMetadata metadata;
	private ComponentBox enclosingBox;
	private ComponentBox innerBox;
	private Class<?> cls;
	private List<Object> dependencies;
	private boolean initialized;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CreatedComponent(Object instance, ComponentMetadata metadata, ComponentBox enclosingBox, ComponentBox innerBox, Class<?> cls) {
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

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Object getInstance() {
		return this.instance;
	}

	public ComponentMetadata getMetadata() {
		return this.metadata;
	}

	public ComponentBox getEnclosingBox() {
		return this.enclosingBox;
	}

	public Class<?> getCls() {
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
