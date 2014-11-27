/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// CindyServletWebAppLoader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 6:31:22 PM
////////

package co.mindie.cindy.webservice;

import co.mindie.cindy.core.component.initializer.ComponentInitializer;
import co.mindie.cindy.core.component.metadata.ComponentMetadataManager;
import co.mindie.cindy.core.component.metadata.ComponentMetadataManagerBuilder;
import co.mindie.cindy.core.component.box.ComponentBox;
import org.apache.log4j.Logger;

/**
 * Creates a CindyWebApp instance with a brand new ComponentMetadataManager.
 */
public abstract class CindyWebAppCreator {

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

	/**
	 * Called during the loading phase. This is where you should load the component metadatas.
	 * @param metadataManagerBuilder The ComponentMetadataManagerBuilder used to load component metadatas.
	 */
	protected abstract void onLoad(ComponentMetadataManagerBuilder metadataManagerBuilder);

	/**
	 * Called after the load, just before the CindyWebAppCreator creates the instance of
	 * CindyWebApp. You can alter the ComponentInitializer to add additional components.
	 * @param initializer The initializer that will be used to create the CindyWebApp instance
	 */
	protected abstract void onCreate(ComponentInitializer initializer);

	public CindyWebApp createApplication() {
		return this.createApplication(false);
	}

	public CindyWebApp createApplication(boolean preloadEndpoints) {
		ComponentMetadataManagerBuilder metadataManagerBuilder = new ComponentMetadataManagerBuilder();

		this.onLoad(metadataManagerBuilder);

		if (this.shouldLoadBuiltinComponents) {
			metadataManagerBuilder.loadComponents("co.mindie.cindy");
		}

		ComponentMetadataManager metadataManager = metadataManagerBuilder.build();

		ComponentInitializer initializer = metadataManager.createInitializer();
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
