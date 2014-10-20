/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.automapping
// Scope.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2014 at 2:08:37 PM
////////

package co.mindie.cindy.automapping;

public enum CreationBox {
	/**
	 * If not found while following the {@link SearchScope} rule,
	 * the dependency will be created in the current {@link co.mindie.cindy.component.ComponentBox}
	 */
	LOCAL,

	/**
	 * If not found while following the {@link SearchScope} rule,
	 * the dependency will be created in a subcontext of the current {@link co.mindie.cindy.component.ComponentBox}.
	 * <p/>
	 * A Component will only create one sub {@link co.mindie.cindy.component.ComponentBox} for its dependencies,
	 * therefore every dependency with this CreationBox will be in the same sub {@link co.mindie.cindy.component.ComponentBox}.
	 */
	SUB_CONTEXT,

	/**
	 * If not found while following the {@link SearchScope} rule,
	 * the dependency will be created in its own private sub {@link co.mindie.cindy.component.ComponentBox}
	 */
	ISOLATED,

	/**
	 * If not found while following the {@link SearchScope} rule,
	 * the dependency will NOT be created
	 */
	NO_CREATION,

	/**
	 * Let another instance decide the creationBox.
	 */
	UNDEFINED
}
