/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// CindyApplicationLoader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 6:31:22 PM
////////

package co.mindie.cindy;

import co.mindie.cindy.component.ComponentContext;
import co.mindie.cindy.component.ComponentMetadataManager;
import co.mindie.cindy.component.ComponentInitializer;

public class CindyAppCreator {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CindyAppCreator() {

	}

	////////////////////////
	// METHODS
	////////////////

	protected void onLoad(CindyApp application) {

	}

	protected CindyApp onCreate(ComponentMetadataManager metadataManager) {
		return new CindyApp(metadataManager);
	}

	public CindyApp createApplication() {
		ComponentMetadataManager metadataManager = new ComponentMetadataManager();
		ComponentInitializer initializer = metadataManager.createInitializer();

		CindyApp application = this.onCreate(metadataManager);

		this.onLoad(application);

		initializer.addCreatedComponent(application, new ComponentContext());

		initializer.init();

		return application;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
