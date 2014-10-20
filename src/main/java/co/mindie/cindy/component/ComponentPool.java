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
	private final boolean useSubComponentContext;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentPool(CindyApp application, Class<T> componentClass, ComponentBox parentComponentBox, boolean useSubComponentContext) {
		this.application = application;
		this.componentClass = componentClass;
		this.parentComponentBox = parentComponentBox;
		this.useSubComponentContext = useSubComponentContext;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	protected PoolableComponent<T> instantiate() {
		ComponentBox componentBox = this.parentComponentBox;
		if (useSubComponentContext) {
			if (componentBox == null) {
				throw new CindyException("A parent ComponentBox must be set to use the SubComponentContext property");
			}
			componentBox = componentBox.createSubComponentContext();
		}

		T component = this.application.createComponent(componentBox, componentClass);
		return new PoolableComponent<>(componentBox, component);
	}
}
