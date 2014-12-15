/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.database.handle.builtin
// SingleHibernateDatabaseHandle.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 9:18:10 PM
////////

package co.mindie.cindy.hibernate.database.handle;

import co.mindie.cindy.core.annotation.Wired;
import co.mindie.cindy.hibernate.database.HibernateDatabase;

public class SimpleHibernateDatabaseHandle extends HibernateDatabaseHandle {

	////////////////////////
	// VARIABLES
	////////////////

	@Wired
	private HibernateDatabase database;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public SimpleHibernateDatabaseHandle() {

	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public HibernateDatabase getHibernateDatabase() {
		return this.database;
	}

}
