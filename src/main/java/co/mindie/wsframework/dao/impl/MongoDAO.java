/////////////////////////////////////////////////
// Project : webservice
// Package : com.ever.webservice.dao.impl
// DataAccessObject.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 6, 2013 at 5:29:28 PM
////////

package co.mindie.wsframework.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import co.mindie.api.model.LazyList;
import co.mindie.wsframework.automapping.Wired;
import co.mindie.wsframework.context.ListProperties;
import co.mindie.wsframework.database.MongoDatabase;
import co.mindie.wsframework.database.handle.MongoDatabaseHandle;
import co.mindie.wsframework.mongo.MongoEntity;
import co.mindie.wsframework.mongo.MongoIterator;
import co.mindie.wsframework.mongo.MongoQuery;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public abstract class MongoDAO<ElementType extends MongoEntity> extends AbstractDAO<ElementType, ObjectId, MongoDatabase> {

	// //////////////////////
	// VARIABLES
	// //////////////

	public static final String DEFAULT_ID_PROPERTY_NAME = "_id";
	public static final String DEFAULT_CREATED_DATE_PROPERTY_NAME = "created_date";
	public static final String DEFAULT_UPDATED_DATE_PROPERTY_NAME = "updated_date";

	private DBCollection collection;
	@Wired private MongoDatabaseHandle databaseHandle;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public MongoDAO(Class<ElementType> managedClass) {
		super(managedClass, DEFAULT_ID_PROPERTY_NAME, DEFAULT_CREATED_DATE_PROPERTY_NAME, DEFAULT_UPDATED_DATE_PROPERTY_NAME);
	}

	// //////////////////////
	// METHODS
	// //////////////

	private String getCollectionName(Class<ElementType> managedType) {
		return managedType.getSimpleName().toLowerCase();
	}

	public abstract ElementType dbObjectToElement(DBObject object);

	public abstract DBObject elementToDBObject(ElementType element);

	public boolean exists(ObjectId key) {
		DBObject query = new MongoQuery().withId(key).build();
		return this.getServerSize(query) > 0;
	}

	public ElementType findForKey(ObjectId key) {
		DBObject query = new MongoQuery().withId(key).build();
		DBObject obj = this.getCollection().findOne(query);
		return this.dbObjectToElement(obj);
	}

	public Serializable save(ElementType element) {
		DateTime currentTimeGMT = DateTime.now();
		element.setCreatedDate(currentTimeGMT);
		element.setUpdatedDate(currentTimeGMT);

		DBObject object = this.elementToDBObject(element);
		WriteResult result = this.getCollection().insert(object);
		ObjectId key = (ObjectId) result.getUpsertedId();

		element.setId(key);

		return key;
	}

	public void update(ElementType element) {
		element.setUpdatedDate(DateTime.now());
		DBObject query = new MongoQuery().withId(element.getId()).build();
		DBObject object = this.elementToDBObject(element);
		this.getCollection().update(query, object);
	}

	public void delete(ElementType element) {
		DBObject query = new MongoQuery().withId(element.getId()).build();
		this.getCollection().remove(query);
	}

	public List<ElementType> findForKeys(List<ObjectId> keys) {
		DBObject query = new MongoQuery().withIds(keys).build();
		DBCursor cursor = this.getCollection().find(query);
		return this.cursorToList(cursor);
	}

	public List<ElementType> findAllOrderAsc() {
		DBCursor cursor = this.findAll(new BasicDBObject("_id", 1), null, null);
		return this.cursorToList(cursor);
	}

	public List<ElementType> findAllOrderDesc() {
		DBCursor cursor = this.findAll(new BasicDBObject("_id", -1), null, null);
		return this.cursorToList(cursor);
	}

	private DBCursor findAll(DBObject orderBy, Integer limit, Integer offset) {
		DBCursor cursor = this.getCollection().find();
		if (offset != null && limit != null) {
			cursor.skip(offset / limit);
			cursor.limit(limit);
		}
		if (orderBy != null) {
			cursor.sort(orderBy);
		}
		return cursor;
	}
//
//	public LazyList<ElementType> findAllOrderAsc(ListProperties listProperties) {
//		DBCursor cursor = this.findAll(new MongoQuery().with("_id", 1).build(), listProperties.limit, listProperties.offset);
//		long serverSize = this.getServerSize(null);
//		return this.cursorToLazyList(cursor, serverSize, listProperties);
//	}
//
//	public LazyList<ElementType> findAllOrderDesc(ListProperties listProperties) {
//		DBCursor cursor = this.findAll(new MongoQuery().with("_id", -1).build(), listProperties.limit, listProperties.offset);
//		long serverSize = this.getServerSize(null);
//		return this.cursorToLazyList(cursor, serverSize, listProperties);
//	}

	public long getTotalCount() {
		return this.getServerSize(null);
	}

	public long getTotalCountBetween(Date fromDate, Date toDate) {
		DBObject query = new MongoQuery().gt("created_date", fromDate).lt("created_date", toDate).build();
		return this.getServerSize(query);
	}

	public long getTotalCountSince(Date date) {
		DBObject query = new MongoQuery().gt("created_date", date).build();
		return this.getServerSize(query);
	}

	public void insertAll(List<ElementType> elements) {
		List<DBObject> objects = new ArrayList<DBObject>();
		for (ElementType element : elements) {
			DateTime currentTimeGMT = DateTime.now();
			element.setCreatedDate(currentTimeGMT);
			element.setUpdatedDate(currentTimeGMT);
			objects.add(this.elementToDBObject(element));
		}
		this.getCollection().insert(objects);
	}

	public List<ElementType> findAllSince(Date date) {
		throw new RuntimeException();
	}

	protected List<ElementType> cursorToList(DBCursor cursor) {
		List<ElementType> elements = new ArrayList<ElementType>();
		while (cursor.hasNext()) {
			DBObject dbObject = cursor.next();
			elements.add(this.dbObjectToElement(dbObject));
		}
		return elements;
	}

	protected MongoIterator<ElementType> cursorToMongoIterator(DBCursor cursor) {
		return new MongoIterator<ElementType>(cursor, this);
	}

//	protected LazyList<ElementType> cursorToLazyList(DBCursor cursor, long serverSize, ListProperties listProperties) {
//		List<ElementType> elements = new ArrayList<ElementType>();
//		while (cursor.hasNext() && elements.size() <= listProperties.limit) {
//			DBObject dbObject = cursor.next();
//			elements.add(this.dbObjectToElement(dbObject));
//		}
//		return new LazyList<ElementType>(elements, listProperties.offset, (int) serverSize);
//	}

	protected long getServerSize(DBObject query) {
		if (query == null)
			return this.getCollection().count();
		else
			return this.getCollection().count(query);
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////


	protected DBCollection getCollection() {
		if (this.collection == null) {
			this.collection = this.databaseHandle.getCollection(this.getCollectionName(this.getManagedClass()));
		}

		return this.collection;
	}
}
