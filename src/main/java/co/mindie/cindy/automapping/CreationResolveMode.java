package co.mindie.cindy.automapping;

/**
 * The creation resolve mode resolves which component should be used when a creation
 * is needed for a type.
 */
public enum CreationResolveMode {

	/**
	 * Makes the component as default for every type it inherits.
	 * If a creation is required for the component type or one of its inherited type,
	 * this component will be used. Creating a component loaded with this mode will fail if
	 * another loaded component has a common inherited with a DEFAULT CreationResolveMode.
	 */
	DEFAULT,

	/**
	 * Makes the component as a fallback for every type it inherits.
	 * If a creation is required for the component type or one of its inherited type,
	 * this component will be used if no other component were loaded with a DEFAULT mode.
	 */
	FALLBACK

}
