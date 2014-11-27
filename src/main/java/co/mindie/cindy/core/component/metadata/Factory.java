package co.mindie.cindy.core.component.metadata;

/**
 * Defines a class responsible for creating a specific type of WSComponents.
 * <p/>
 * If no Component can be Wired, the Application will try to create a new one from this factory.
 *
 * @param <T> The type of CindyComponent handled by the factory
 */
public interface Factory<T> {

	T createComponent();

}
