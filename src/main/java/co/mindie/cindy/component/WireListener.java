package co.mindie.cindy.component;

/**
 * Classes that implement this interface will have the following declared methods
 * called at initialization time on some events.
 */
public interface WireListener {

	/**
	 * Called before the Component will have its dependencies wired.
	 * The component is free to add/remove any dependency from its loaded
	 * metadata during this time.
	 */
	void onWillWire(ComponentInitializer initializer);

	/**
	 * Called after the Component has its dependencies wired.
	 * Note that the Component is not ready to use yet, if you
	 * want to know when it is, implement the Initializable interface.
	 */
	void onWired(ComponentInitializer initializer);

}
