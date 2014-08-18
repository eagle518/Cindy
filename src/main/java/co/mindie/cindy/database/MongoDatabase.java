/////////////////////////////////////////////////
// Project : Mindie WebService
// Package : co.mindie.databases
// Database.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 29, 2014 at 7:24:27 PM
////////

package co.mindie.cindy.database;

import co.mindie.cindy.mongo.JodaDateTimeBSONTransformer;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.bson.BSON;
import org.joda.time.DateTime;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;

public class MongoDatabase extends Database {

	// //////////////////////
	// VARIABLES
	// //////////////

	private static final Object HOOKS_LOCK = new Object();
	private static boolean hooksAdded = false;
	private MongoClient mongoClient;
	private DB db;
	private String mongoConnectionString;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public MongoDatabase() {
		super();
		this.initHooks();
	}

	private void initHooks() {
		if (!hooksAdded) {
			synchronized (HOOKS_LOCK) {
				if (!hooksAdded) {
					// Adding a hook to handle java.util.Date and org.joda.time.DateTime
					BSON.addEncodingHook(DateTime.class, new JodaDateTimeBSONTransformer());
					BSON.addDecodingHook(Date.class, new JodaDateTimeBSONTransformer());
					hooksAdded = true;
				}
			}
		}
	}

	// //////////////////////
	// METHODS
	// //////////////

	private void loadClient() {
		if (this.mongoClient == null) {
			synchronized (this) {
				if (this.mongoClient == null) {
					String connectionString = this.mongoConnectionString;
					try {
						MongoClientURI uri = new MongoClientURI(connectionString);
						MongoClient mongoClient = new MongoClient(uri);
						this.db = mongoClient.getDB(uri.getDatabase());
						this.mongoClient = mongoClient;
					} catch (UnknownHostException e) {
						throw new RuntimeException("Unable to connect to Mongo Database with uri=" + connectionString, e);
					}
				}
			}
		}
	}

	public final MongoConnection openConnection() {
		this.loadClient();

		return new MongoConnection(this.db, this.mongoClient);
	}

	@Override
	public void close() throws IOException {
		if (this.mongoClient != null) {
			this.mongoClient.close();
			this.db = null;
			this.mongoClient = null;
		}
	}

	public String getMongoConnectionString() {
		return this.mongoConnectionString;
	}

	public void setMongoConnectionString(String mongoConnectionString) {
		this.mongoConnectionString = mongoConnectionString;
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

}
