package co.mindie.cindy.async;

import co.mindie.cindy.async.manager.AsyncTaskManager;
import co.mindie.cindy.core.module.Module;

/**
 * Created by simoncorsin on 09/01/15.
 */
public class AsyncModule implements Module {

	////////////////////////
	// VARIABLES
	////////////////

	private AsyncModuleConfiguration asyncModuleConfiguration;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public AsyncModule() {
		this.asyncModuleConfiguration = new AsyncModuleConfiguration();
	}

	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public AsyncModuleConfiguration getConfiguration() {
		return this.asyncModuleConfiguration;
	}

	@Override
	public Class<?>[] getComponentClasses() {
		return new Class<?>[] { AsyncTaskManager.class };
	}

	@Override
	public String[] getComponentsClasspaths() {
		return new String[0];
	}

	@Override
	public Module[] getDependencies() {
		return null;
	}

}
