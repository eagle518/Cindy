/////////////////////////////////////////////////
// Project : Ever WebService
// Package : com.ever.wsframework.json
// JacksonJsonNode.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Sep 14, 2013 at 2:29:24 PM
////////

package co.mindie.cindy.json;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;

public class JacksonJsonNode implements IJsonNode {

	////////////////////////
	// VARIABLES
	////////////////

	private JsonNode node;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public JacksonJsonNode(JsonNode node) {
		if (node == null) {
			throw new NullPointerException("rootNode");
		}

		this.node = node;
	}

	////////////////////////
	// METHODS
	////////////////

	private IJsonNode[] addItem(IJsonNode[] array, IJsonNode object) {
		final IJsonNode[] newArray = new IJsonNode[array.length + 1];

		System.arraycopy(array, 0, newArray, 0, array.length);
		newArray[array.length] = object;

		return newArray;
	}

	private JsonNode getNodeForPath(String path) {
		JsonNode currentNode = this.node;
		String[] paths = path.split("->|\\[");

		for (String currentPath : paths) {
			if (currentPath.endsWith("]")) {
				currentPath = currentPath.replace("]", "");
				int indexAsked = Integer.parseInt(currentPath);

				Iterator<JsonNode> it = currentNode.elements();
				currentNode = null;

				int currentIndex = 0;
				while (it.hasNext()) {
					JsonNode node = it.next();

					if (currentIndex == indexAsked) {
						currentNode = node;
						break;
					}

					currentIndex++;
				}
			} else {
				currentNode = currentNode.get(currentPath);
			}

			if (currentNode == null) {
				break;
			}
		}

		return currentNode;
	}

	@Override
	public IJsonNode getNode(String path) {
		JsonNode node = this.getNodeForPath(path);

		return node != null ? new JacksonJsonNode(node) : null;
	}

	@Override
	public IJsonNode[] getChildren() {
		IJsonNode[] wrappedElements = new IJsonNode[0];

		Iterator<JsonNode> it = this.node.elements();

		while (it.hasNext()) {
			JsonNode node = it.next();
			wrappedElements = addItem(wrappedElements, new JacksonJsonNode(node));
		}

		return wrappedElements;
	}

	@Override
	public boolean asBoolean() {
		return this.node.asBoolean();
	}

	@Override
	public int asInteger() {
		return this.node.asInt();
	}

	@Override
	public double asDouble() {
		return this.node.asDouble();
	}

	@Override
	public String asString() {
		return this.node.asText();
	}

	@Override
	public Boolean getBooleanForPath(String path) {
		IJsonNode node = this.getNode(path);

		return node != null ? node.asBoolean() : null;
	}

	@Override
	public Integer getIntegerForPath(String path) {
		IJsonNode node = this.getNode(path);

		return node != null ? node.asInteger() : null;
	}

	@Override
	public Double getDoubleForPath(String path) {
		IJsonNode node = this.getNode(path);

		return node != null ? node.asDouble() : null;
	}

	@Override
	public String getStringForPath(String path) {
		IJsonNode node = this.getNode(path);

		return node != null ? node.asString() : null;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
