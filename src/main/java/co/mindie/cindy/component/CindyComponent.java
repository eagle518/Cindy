/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// WSApplicationObject.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 7, 2014 at 11:11:25 AM
////////

package co.mindie.cindy.component;

import co.mindie.cindy.CindyApp;
import co.mindie.cindy.utils.Initializable;

public class CindyComponent implements Initializable {

	////////////////////////
	// VARIABLES
	////////////////

	private CindyApp application;
	private ComponentContext componentContext;
	private boolean initialized;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CindyComponent() {

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

	public CindyApp getApplication() {
		return this.application;
	}

	public void setApplication(CindyApp application) {
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
