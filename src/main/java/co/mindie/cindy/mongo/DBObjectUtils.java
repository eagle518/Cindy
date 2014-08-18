package co.mindie.cindy.mongo;

import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.Map;

// TODO Why don't cast the type directly?
public class DBObjectUtils {

	// //////////////////////
	// VARIABLES
	// //////////////

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	// //////////////////////
	// METHODS
	// //////////////

	private static <T> T getAs(DBObject obj, String key, Class<T> clazz) {
		Object res = obj.get(key);
		if (res != null)
			return clazz.cast(res);
		return null;
	}

	public static ObjectId getAsObjectId(DBObject obj, String key) {
		return getAs(obj, key, ObjectId.class);
	}

	public static DateTime getAsDate(DBObject obj, String key) {
		// Fix for the old values in the database
		if (obj.get(key) instanceof Date) {
			return new DateTime(obj.get(key));
		}
		return getAs(obj, key, DateTime.class);
	}

	public static Boolean getAsBoolean(DBObject obj, String key) {
		return getAs(obj, key, Boolean.class);
	}

	public static Long getAsLong(DBObject obj, String key) {
		return getAs(obj, key, Long.class);
	}

	public static String getAsString(DBObject obj, String key) {
		return getAs(obj, key, String.class);
	}

	public static Integer getAsInteger(DBObject obj, String key) {
		return getAs(obj, key, Integer.class);
	}

	public static Float getAsFloat(DBObject obj, String key) {
		return getAs(obj, key, Float.class);
	}

	public static Double getAsDouble(DBObject obj, String key) {
		return getAs(obj, key, Double.class);
	}

	public static DBObject getAsDBObject(DBObject obj, String key) {
		return getAs(obj, key, DBObject.class);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAsMap(DBObject obj, String key) {
		return getAs(obj, key, Map.class);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getAsMapOfStrings(DBObject obj, String key) {
		return getAs(obj, key, Map.class);
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////
}
