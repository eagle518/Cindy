package co.mindie.cindy.component;

import me.corsin.javatools.misc.PoolableImpl;

import java.io.Closeable;

public class PoolableComponent<T> extends PoolableImpl implements Closeable {
	////////////////////////
	// VARIABLES
	////////////////

	private final ComponentContext componentContext;
	private final T component;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public PoolableComponent(ComponentContext componentContext, T component) {
		this.componentContext = componentContext;
		this.component = component;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void release() {
		this.close();
		super.release();
	}

	@Override
	public void close() {
		if (this.componentContext != null) {
			this.componentContext.close();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


	public T getComponent() {
		return component;
	}
}