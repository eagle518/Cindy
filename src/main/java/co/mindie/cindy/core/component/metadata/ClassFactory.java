package co.mindie.cindy.core.component.metadata;

import co.mindie.cindy.core.component.metadata.Factory;
import co.mindie.cindy.core.exception.CindyException;

/**
 * Created by simoncorsin on 20/11/14.
 */
public class ClassFactory<T> implements Factory<T> {

	////////////////////////
	// VARIABLES
	////////////////

	final private Class<T> cls;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ClassFactory(Class<T> cls) {
		this.cls = cls;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public T createComponent() {
		try {
			return this.cls.newInstance();
		} catch (Exception e) {
			throw new CindyException("Unable to create instance of " + this.cls, e);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
