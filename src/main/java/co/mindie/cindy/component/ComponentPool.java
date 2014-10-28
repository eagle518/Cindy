package co.mindie.cindy.component;

import me.corsin.javatools.misc.SynchronizedPool;

public class ComponentPool<T> extends SynchronizedPool<PoolableComponent<T>> {

	////////////////////////
	// VARIABLES
	////////////////

	final private ComponentMetadataManager metadataManager;
	private final Class<T> componentClass;
	private final ComponentBox parentComponentBox;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentPool(ComponentMetadataManager metadataManager, Class<T> componentClass, ComponentBox componentBox) {
		this.metadataManager = metadataManager;
		this.componentClass = componentClass;
		this.parentComponentBox = componentBox;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	protected PoolableComponent<T> instantiate() {
		ComponentBox componentBox = this.parentComponentBox;

		return new PoolableComponent<>(this.metadataManager.createComponent(componentClass, componentBox));
	}
}
