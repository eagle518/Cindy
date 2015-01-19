/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.dao.impl.builtin
// SimpleHibernateDAO.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 9:20:04 PM
////////

package co.mindie.cindy.hibernate.builtin;

import co.mindie.cindy.core.component.SearchScope;
import co.mindie.cindy.core.annotation.Wired;
import co.mindie.cindy.hibernate.dao.HibernateDAO;
import co.mindie.cindy.hibernate.database.handle.HibernateDatabaseHandle;
import co.mindie.cindy.core.tools.Initializable;

import java.io.Serializable;

public abstract class SimpleHibernateDAO<PrimaryKey extends Serializable, ElementType> extends HibernateDAO<PrimaryKey, ElementType> implements Initializable{

	////////////////////////
	// VARIABLES
	////////////////

	@Wired(searchScope = SearchScope.LOCAL) private HibernateDatabaseHandle databaseHandle;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public SimpleHibernateDAO(Class<ElementType> managedClass) {
		super(managedClass);
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void init() {
		this.setDatabaseHandle(this.databaseHandle);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
