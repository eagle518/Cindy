/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// CindyServletWebAppLoader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 6:31:22 PM
////////

package co.mindie.cindy;

import co.mindie.cindy.component.box.ComponentBox;
import co.mindie.cindy.component.ComponentMetadataManager;
import co.mindie.cindy.component.ComponentInitializer;
import org.apache.log4j.Logger;

public class CindyWebAppCreator {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(CindyWebAppCreator.class);
	private boolean shouldLoadBuiltinComponents;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CindyWebAppCreator() {
		this.shouldLoadBuiltinComponents = true;
	}

	////////////////////////
	// METHODS
	////////////////

	protected void onLoad(ComponentMetadataManager metadataManager) {

	}

	protected void onCreate(ComponentInitializer initializer) {

	}

	public CindyWebApp createApplication() {
		return this.createApplication(false);
	}

	public CindyWebApp createApplication(boolean preloadEndpoints) {
		ComponentMetadataManager metadataManager = new ComponentMetadataManager();
		ComponentInitializer initializer = metadataManager.createInitializer();

		this.onLoad(metadataManager);

		if (this.shouldLoadBuiltinComponents) {
			metadataManager.loadComponents("co.mindie.cindy");
		}

		this.onCreate(initializer);

		CindyWebApp webApp = initializer.createComponent(CindyWebApp.class, ComponentBox.create(true)).getInstance();

		initializer.init();

		if (preloadEndpoints) {
			webApp.getControllerManager().preloadEndpoints();
		}

		return webApp;
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
