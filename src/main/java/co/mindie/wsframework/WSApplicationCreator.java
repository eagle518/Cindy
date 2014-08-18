/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// WSApplicationLoader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 6:31:22 PM
////////

package co.mindie.wsframework;

import co.mindie.wsframework.component.ComponentContext;
import co.mindie.wsframework.component.ComponentInitializer;
import co.mindie.wsframework.component.ComponentMetadataManager;

public class WSApplicationCreator {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public WSApplicationCreator() {

	}

	////////////////////////
	// METHODS
	////////////////

	protected void onLoad(WSApplication application) {

	}

	protected WSApplication onCreate(ComponentMetadataManager metadataManager) {
		return new WSApplication(metadataManager);
	}

	public WSApplication createApplication() {
		ComponentMetadataManager metadataManager = new ComponentMetadataManager();
		ComponentInitializer initializer = metadataManager.createInitializer();

		WSApplication application = this.onCreate(metadataManager);

		this.onLoad(application);

		initializer.addCreatedComponent(application, new ComponentContext());

		initializer.init();

		return application;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
