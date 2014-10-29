/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.responseserializer
// JsonResponseSerializer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Feb 24, 2014 at 1:46:07 PM
////////

package co.mindie.cindy.responseserializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import co.mindie.cindy.automapping.Load;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;

@Load(creationPriority = -1)
public class JsonResponseWriter implements IResponseWriter {

	// //////////////////////
	// VARIABLES
	// //////////////

	private boolean indentEnabled;
	private ObjectMapper mapper;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public JsonResponseWriter() {
		this.mapper = new ObjectMapper();

		this.mapper.registerModule(new ObjectIdJsonModule());
		this.mapper.registerModule(new JodaModule());
		this.mapper.registerModule(new JsonOrgModule());

		this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

		this.mapper.setSerializationInclusion(Include.NON_NULL);

		this.mapper.setDateFormat(new ISO8601DateFormat());
	}

	// //////////////////////
	// METHODS
	// //////////////

	@Override
	public void writeResponse(Object response, OutputStream outputStream) throws IOException {
		if (response == null) {
			response = new HashMap<>();
		}

		this.getObjectMapper().writeValue(outputStream, response);
	}

	// //////////////////////
	// METHODS
	// //////////////

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	public boolean isIndentEnabled() {
		return this.indentEnabled;
	}

	public void setIndentEnabled(boolean indentEnabled) {
		this.indentEnabled = indentEnabled;
		if (indentEnabled) {
			this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
		} else {
			this.mapper.disable(SerializationFeature.INDENT_OUTPUT);
		}
	}

	@Override
	public String getContentType() {
		return "application/json";
	}

	@Override
	public Long getContentLength(Object response) {
		return null;
	}

	public ObjectMapper getObjectMapper() {
		return this.mapper;
	}
}
