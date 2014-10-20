/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// CindyServletApplicationLoader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 6:31:22 PM
////////

package co.mindie.cindy;

import co.mindie.cindy.component.ComponentBox;
import co.mindie.cindy.component.ComponentMetadataManager;
import co.mindie.cindy.component.ComponentInitializer;
import org.apache.log4j.Logger;

public class CindyAppCreator {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(CindyAppCreator.class);
	private boolean shouldLoadBuiltinComponents;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CindyAppCreator() {
		this.shouldLoadBuiltinComponents = true;
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
		return this.createApplication(false);
	}

	public CindyApp createApplication(boolean preloadEndpoints) {
		ComponentMetadataManager metadataManager = new ComponentMetadataManager();
		ComponentInitializer initializer = metadataManager.createInitializer();

		CindyApp application = this.onCreate(metadataManager);

		this.onLoad(application);

		if (this.shouldLoadBuiltinComponents) {
			application.scanForComponents("co.mindie.cindy");
		}

		initializer.addCreatedComponent(application, new ComponentBox());

		initializer.init();

		if (preloadEndpoints) {
			application.getControllerManager().preloadEndpoints();
		}

		return application;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public boolean shouldLoadBuiltinComponents() {
		return shouldLoadBuiltinComponents;
	}

	public void setShouldLoadBuiltinComponents(boolean shouldLoadBuiltinComponents) {
		this.shouldLoadBuiltinComponents = shouldLoadBuiltinComponents;
	}
}
