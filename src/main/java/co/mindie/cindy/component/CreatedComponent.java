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
	private ComponentBox context;
	private Class<?> cls;
	private List<Object> dependencies;
	private boolean initialized;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CreatedComponent(Object instance, ComponentMetadata metadata, ComponentBox context, Class<?> cls) {
		this.instance = instance;
		this.metadata = metadata;
		this.context = context;
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

	public ComponentBox getContext() {
		return this.context;
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
}
