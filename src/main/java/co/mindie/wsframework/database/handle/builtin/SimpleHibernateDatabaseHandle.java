/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.database.handle.builtin
// SingleHibernateDatabaseHandle.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 9:18:10 PM
////////

package co.mindie.wsframework.database.handle.builtin;

import co.mindie.wsframework.automapping.Wired;
import co.mindie.wsframework.database.HibernateDatabase;
import co.mindie.wsframework.database.handle.HibernateDatabaseHandle;

public class SimpleHibernateDatabaseHandle extends HibernateDatabaseHandle {

	////////////////////////
	// VARIABLES
	////////////////

	@Wired private HibernateDatabase database;

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
