/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.component
// ComponentDependency.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 16, 2014 at 1:05:13 PM
////////

package co.mindie.wsframework.component;

import co.mindie.wsframework.automapping.CreationScope;
import co.mindie.wsframework.automapping.SearchScope;

public class ComponentDependency {

	////////////////////////
	// VARIABLES
	////////////////

	final private Class<?> componentClass;
	private boolean required;
	private boolean list;
	private SearchScope searchScope;
	private CreationScope creationScope;
	private Wire wire;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentDependency(Class<?> componentClass, boolean isList, SearchScope searchScope, CreationScope creationScope) {
		this.componentClass = componentClass;
		this.list = isList;
		this.searchScope = searchScope;
		this.creationScope = creationScope;
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public SearchScope getSearchScope() {
		return this.searchScope;
	}

	public void setSearchScope(SearchScope searchScope) {
		this.searchScope = searchScope;
	}

	public Class<?> getComponentClass() {
		return this.componentClass;
	}

	public boolean isRequired() {
		return this.required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isList() {
		return this.list;
	}

	public CreationScope getCreationScope() {
		return this.creationScope;
	}

	public Wire getWire() {
		return this.wire;
	}

	public void setWire(Wire wire) {
		this.wire = wire;
	}
}
