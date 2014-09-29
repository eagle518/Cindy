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
	private final ComponentContext parentComponentContext;
	private final boolean useSubComponentContext;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentPool(CindyApp application, Class<T> componentClass, ComponentContext parentComponentContext, boolean useSubComponentContext) {
		this.application = application;
		this.componentClass = componentClass;
		this.parentComponentContext = parentComponentContext;
		this.useSubComponentContext = useSubComponentContext;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	protected PoolableComponent<T> instantiate() {
		ComponentContext componentContext = this.parentComponentContext;
		if (useSubComponentContext) {
			if (componentContext == null) {
				throw new CindyException("A parent ComponentContext must be set to use the SubComponentContext property");
			}
			componentContext = componentContext.createSubComponentContext();
		}

		T component = this.application.createComponent(componentContext, componentClass);
		return new PoolableComponent<>(componentContext, component);
	}
}
