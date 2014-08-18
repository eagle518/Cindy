/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.database
// MongoConnection.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on May 14, 2014 at 6:00:15 PM
////////

package co.mindie.wsframework.database;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.io.Closeable;

public class MongoConnection implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////

	private DB database;
	private MongoClient client;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public MongoConnection(DB database, MongoClient client) {
		this.database = database;
		this.client = client;
	}

	////////////////////////
	// METHODS
	////////////////

	public void close() {
		if (this.client != null) {
			this.database = null;
			this.client = null;
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public DB getDatabase() {
		return this.database;
	}

	public MongoClient getClient() {
		return this.client;
	}
}
