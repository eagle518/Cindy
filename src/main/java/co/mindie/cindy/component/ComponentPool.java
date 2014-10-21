package co.mindie.cindy.component;

import co.mindie.cindy.CindyApp;
import co.mindie.cindy.exception.CindyException;
import me.corsin.javatools.misc.SynchronizedPool;

public class ComponentPool<T> extends SynchronizedPool<PoolableComponent<T>> {

	////////////////////////
	// VARIABLES
	////////////////

	private final CindyApp application;
	private final Class<T> componentClass;
	private final ComponentBox parentComponentBox;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentPool(CindyApp application, Class<T> componentClass, ComponentBox parentComponentBox) {
		this.application = application;
		this.componentClass = componentClass;
		this.parentComponentBox = parentComponentBox;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	protected PoolableComponent<T> instantiate() {
		ComponentBox componentBox = this.parentComponentBox;

		// TODO NOT THREAD SAFE
		T component = this.application.createComponent(componentBox, componentClass);
		return new PoolableComponent<>(componentBox, component);
	}
}
