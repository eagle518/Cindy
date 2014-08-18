package co.mindie.wsframework.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.util.List;

public class MongoQuery {
	// //////////////////////
	// VARIABLES
	// //////////////

	private final DBObject query;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public MongoQuery() {
		this.query = new BasicDBObject();
	}

	// //////////////////////
	// METHODS
	// //////////////

	public MongoQuery withId(ObjectId id) {
		return with("_id", id);
	}

	public MongoQuery withIds(List<ObjectId> keys) {
		ObjectId[] array = keys.toArray(new ObjectId[keys.size()]);
		return in("_id", array);
	}

	public DBObject build() {
		return query;
	}

	public MongoQuery in(String key, Object[] objs) {
		return with("_id", new BasicDBObject("$in", objs));
	}

	public MongoQuery gt(String key, Object obj) {
		return with(key, new BasicDBObject("$gt", obj));
	}

	public MongoQuery lt(String key, Object obj) {
		return with(key, new BasicDBObject("$lt", obj));
	}

	public MongoQuery with(String key, Object obj) {
		this.query.put(key, obj);
		return this;
	}

	public MongoQuery between(String key, Object obj1, Object obj2) {
		DBObject obj = new BasicDBObject();
		obj.put("$gt", obj1);
		obj.put("$lt", obj2);
		return with(key, obj);
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////
}
