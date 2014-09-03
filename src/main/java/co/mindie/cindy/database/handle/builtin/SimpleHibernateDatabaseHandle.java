/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.database.handle.builtin
// SingleHibernateDatabaseHandle.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 29, 2014 at 9:18:10 PM
////////

package co.mindie.cindy.database.handle.builtin;

import co.mindie.cindy.automapping.Component;
import co.mindie.cindy.automapping.CreationResolveMode;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.database.HibernateDatabase;
import co.mindie.cindy.database.handle.HibernateDatabaseHandle;

@Component(creationResolveMode = CreationResolveMode.FALLBACK)
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
