package co.mindie.cindy.webservice.responsewriter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdJsonModule extends SimpleModule {
	private static final long serialVersionUID = 4655672431851353824L;

	public ObjectIdJsonModule() {
		this.addSerializer(ObjectId.class, new ObjectIdJsonSerializer());
		this.addDeserializer(ObjectId.class, new ObjectIdJsonDeserializer());
	}

	public class ObjectIdJsonSerializer extends JsonSerializer<ObjectId> {
		@Override
		public void serialize(ObjectId obj, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
			if (obj == null)
				generator.writeNull();
			else
				generator.writeString(obj.toString());
		}
	}

	public class ObjectIdJsonDeserializer extends JsonDeserializer<ObjectId> {
		@Override
		public ObjectId deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
			String id = parser.getText();
			return new ObjectId(id);
		}
	}
}
