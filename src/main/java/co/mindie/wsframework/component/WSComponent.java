/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// WSApplicationObject.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 7, 2014 at 11:11:25 AM
////////

package co.mindie.wsframework.component;

import co.mindie.wsframework.WSApplication;
import co.mindie.wsframework.utils.Initializable;

public class WSComponent implements Initializable {

	////////////////////////
	// VARIABLES
	////////////////

	private WSApplication application;
	private ComponentContext componentContext;
	private boolean initialized;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public WSComponent() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void init() {
		this.initialized = true;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public WSApplication getApplication() {
		return this.application;
	}

	public void setApplication(WSApplication application) {
		this.application = application;
	}

	public ComponentContext getComponentContext() {
		return this.componentContext;
	}

	public void setComponentContext(ComponentContext componentContext) {
		this.componentContext = componentContext;
	}

	@Override
	public boolean isInitialized() {
		return this.initialized;
	}

}
