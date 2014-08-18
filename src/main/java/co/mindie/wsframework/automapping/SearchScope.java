/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.automapping
// Scope.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2014 at 2:08:37 PM
////////

package co.mindie.wsframework.automapping;

public enum SearchScope {
	/**
	 * The dependency will be searched in the current {@link co.mindie.wsframework.component.ComponentContext} and its parents.
	 */
	GLOBAL,

	/**
	 * The dependency will be searched in the current {@link co.mindie.wsframework.component.ComponentContext} only.
	 */
	LOCAL,

	/**
	 * The dependency will not be searched.
	 */
	NO_SEARCH,

	/**
	 * Let another instance decide the searchScope.
	 */
	UNDEFINED

}
