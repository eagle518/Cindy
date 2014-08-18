/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.context
// SessionHandle.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 29, 2014 at 7:01:46 PM
////////

package co.mindie.cindy.database.handle;

import co.mindie.cindy.automapping.Component;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.component.CindyComponent;
import co.mindie.cindy.database.Database;
import co.mindie.cindy.database.MongoDatabase;
import co.mindie.cindy.database.MongoConnection;
import com.mongodb.DBCollection;

@Component
public class MongoDatabaseHandle extends CindyComponent implements IDatabaseHandle {

	// //////////////////////
	// VARIABLES
	// //////////////

	@Wired
	private MongoDatabase database;
	private MongoConnection mongoConnection;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	// //////////////////////
	// METHODS
	// //////////////

	@Override
	public void close() {
		if (this.mongoConnection != null) {
			this.mongoConnection.close();
			this.mongoConnection = null;
		}
	}

	public DBCollection getCollection(String collectionName) {
		return this.getConnection().getDatabase().getCollection(collectionName);
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	public MongoConnection getConnection() {
		if (this.mongoConnection == null) {
			this.mongoConnection = this.database.openConnection();
		}

		return this.mongoConnection;
	}

	@Override
	public Database getDatabase() {
		return this.database;
	}
}
