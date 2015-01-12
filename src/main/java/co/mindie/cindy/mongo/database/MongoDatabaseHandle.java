/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.context
// SessionHandle.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 29, 2014 at 7:01:46 PM
////////

package co.mindie.cindy.mongo.database;

import co.mindie.cindy.core.annotation.Aspects;
import co.mindie.cindy.core.annotation.Wired;
import co.mindie.cindy.hibernate.database.Database;
import com.mongodb.DBCollection;

import java.io.Closeable;

@Aspects
public class MongoDatabaseHandle implements Closeable {

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

	public MongoDatabase getDatabase() {
		return this.database;
	}
}
