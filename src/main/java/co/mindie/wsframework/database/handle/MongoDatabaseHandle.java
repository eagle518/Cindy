/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.context
// SessionHandle.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 29, 2014 at 7:01:46 PM
////////

package co.mindie.wsframework.database.handle;

import co.mindie.wsframework.automapping.Component;
import co.mindie.wsframework.automapping.Wired;
import co.mindie.wsframework.component.WSComponent;
import co.mindie.wsframework.database.MongoConnection;
import co.mindie.wsframework.database.MongoDatabase;
import co.mindie.wsframework.database.WSDatabase;
import com.mongodb.DBCollection;

@Component
public class MongoDatabaseHandle extends WSComponent implements IDatabaseHandle {

	// //////////////////////
	// VARIABLES
	// //////////////

	@Wired private MongoDatabase database;
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
	public WSDatabase getDatabase() {
		return this.database;
	}
}
