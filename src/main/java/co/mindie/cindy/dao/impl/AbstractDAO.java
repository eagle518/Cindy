/////////////////////////////////////////////////
// Project : webservice
// Package : com.ever.webservice.dao.impl
// DataAccessObject.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 6, 2013 at 5:29:28 PM
////////

package co.mindie.cindy.dao.impl;

import java.io.Serializable;

import co.mindie.cindy.service.Service;

public abstract class AbstractDAO<ElementType, PrimaryKey extends Serializable, DatabaseType> extends Service {

	// //////////////////////
	// VARIABLES
	// //////////////

	private final String primaryKeyPropertyName;
	private final String createdDatePropertyName;
	private final String updatedDatePropertyName;
	private final Class<ElementType> managedClass;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public AbstractDAO(Class<ElementType> managedClass, String primaryKeyPropertyName, String createdDatePropertyName, String updatedDatePropertyName) {
		this.managedClass = managedClass;
		this.primaryKeyPropertyName = primaryKeyPropertyName;
		this.createdDatePropertyName = createdDatePropertyName;
		this.updatedDatePropertyName = updatedDatePropertyName;
	}

	// //////////////////////
	// METHODS
	// //////////////

	@Override
	public void close() {

	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	/**
	 * Returns the name of the property that hold the primary key value.
	 *
	 * @return
	 */
	public String getPrimaryKeyPropertyName() {
		return this.primaryKeyPropertyName;
	}

	public String getCreatedDatePropertyName() {
		return this.createdDatePropertyName;
	}

	public String getUpdatedDatePropertyName() {
		return this.updatedDatePropertyName;
	}

	public Class<ElementType> getManagedClass() {
		return this.managedClass;
	}
}
