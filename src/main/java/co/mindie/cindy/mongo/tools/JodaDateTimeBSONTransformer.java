package co.mindie.cindy.mongo.tools;

import org.bson.Transformer;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Custom BSON Transformer providing serialization/deserialization from/to java.util.Date and org.joda.time.DateTime
 */
public class JodaDateTimeBSONTransformer implements Transformer {
	@Override
	public Object transform(Object o) {
		if (o instanceof DateTime) {
			return ((DateTime) o).toDate();
		} else if (o instanceof Date) {
			return new DateTime(o);
		}
		throw new IllegalArgumentException("JodaDateTimeBSONTransformer can only be used for java.util.Date or org.joda.time.DateTime");
	}
}
