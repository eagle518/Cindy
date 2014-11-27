package co.mindie.cindy.core.component.tools;

import co.mindie.cindy.core.component.initializer.CreatedComponent;
import me.corsin.javatools.misc.PoolableImpl;

import java.io.Closeable;

public class PoolableComponent<T> extends PoolableImpl implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////

	private final CreatedComponent<T> createdComponent;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public PoolableComponent(CreatedComponent<T> createdComponent) {
		this.createdComponent = createdComponent;
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
		this.createdComponent.close();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public T getComponent() {
		return this.createdComponent.getInstance();
	}
}