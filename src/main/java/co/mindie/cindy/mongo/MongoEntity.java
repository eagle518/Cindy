package co.mindie.cindy.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

// TODO inheritance from entity
public abstract class MongoEntity {
	// //////////////////////
	// VARIABLES
	// //////////////

	@JsonProperty(value = "id")
	private ObjectId id;
	@JsonProperty(value = "created_date")
	private DateTime createdDate;
	@JsonProperty(value = "updated_date")
	private DateTime updatedDate;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	// //////////////////////
	// METHODS
	// //////////////

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public DateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(DateTime createdDate) {
		this.createdDate = createdDate;
	}

	public DateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(DateTime updatedDate) {
		this.updatedDate = updatedDate;
	}
}
