package co.mindie.wsframework.mongo;

import co.mindie.wsframework.dao.impl.MongoDAO;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.io.Closeable;
import java.util.Iterator;

public class MongoIterator<T extends MongoEntity> implements Iterator<T>, Closeable {
	private final MongoDAO<T> dao;
	private DBCursor cursor;

	public MongoIterator(DBCursor cursor, MongoDAO<T> dao) {
		this.cursor = cursor;
		this.dao = dao;
	}

	@Override
	public boolean hasNext() {
		return this.cursor.hasNext();
	}

	@Override
	public T next() {
		DBObject obj = cursor.next();
		return this.dao.dbObjectToElement(obj);
	}

	@Override
	public void remove() {
		this.cursor.remove();
	}

	@Override
	public void close() {
		if (this.cursor != null) {
			this.cursor.close();
			this.cursor = null;
		}
	}

}
