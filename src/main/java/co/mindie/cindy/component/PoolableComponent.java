package co.mindie.cindy.component;

import me.corsin.javatools.misc.PoolableImpl;

import java.io.Closeable;

public class PoolableComponent<T> extends PoolableImpl implements Closeable {
	////////////////////////
	// VARIABLES
	////////////////

	private final ComponentBox componentBox;
	private final T component;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public PoolableComponent(ComponentBox componentBox, T component) {
		this.componentBox = componentBox;
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
		if (this.componentBox != null) {
			this.componentBox.close();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


	public T getComponent() {
		return component;
	}
}