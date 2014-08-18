/////////////////////////////////////////////////
// Project : Ever WebService
// Package : com.ever.wsframework.json
// JsonContent.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Sep 13, 2013 at 4:32:29 PM
////////

package co.mindie.cindy.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class JsonContent extends JacksonJsonNode {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public JsonContent(JsonNode rootNode) {
		super(rootNode);
	}

	////////////////////////
	// METHODS
	////////////////

	public static JsonContent fromStringPath(String stringPath) throws IOException {
		return fromURL(new URL(stringPath));
	}

	public static JsonContent fromURL(URL url) throws IOException {
		InputStream inputStream = url.openStream();
		try {
			return fromStream(inputStream);
		} finally {
			inputStream.close();
		}
	}

	public static JsonContent fromStream(InputStream inputStream) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readValue(inputStream, JsonNode.class);

		return new JsonContent(rootNode);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
