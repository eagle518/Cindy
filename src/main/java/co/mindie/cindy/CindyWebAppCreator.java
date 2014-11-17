/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// CindyServletWebAppLoader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 6:31:22 PM
////////

package co.mindie.cindy;

import co.mindie.cindy.component.ComponentInitializer;
import co.mindie.cindy.component.ComponentMetadataManager;
import co.mindie.cindy.component.box.ComponentBox;
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
	 * @param metadataManager The ComponentMetadataManager used to load component metadatas.
	 */
	protected abstract void onLoad(ComponentMetadataManager metadataManager);

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
		ComponentMetadataManager metadataManager = new ComponentMetadataManager();
		ComponentInitializer initializer = metadataManager.createInitializer();

		this.onLoad(metadataManager);

		if (this.shouldLoadBuiltinComponents) {
			metadataManager.loadComponents("co.mindie.cindy");
		}

		metadataManager.ensureIntegrity();

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
