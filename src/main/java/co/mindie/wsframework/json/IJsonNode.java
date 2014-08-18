/////////////////////////////////////////////////
// Project : Ever WebService
// Package : com.ever.wsframework.json
// IJsonContent.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Sep 13, 2013 at 6:30:38 PM
////////

package co.mindie.wsframework.json;

public interface IJsonNode {

	IJsonNode getNode(String path);

	IJsonNode[] getChildren();

	boolean asBoolean();

	int asInteger();

	double asDouble();

	String asString();

	Boolean getBooleanForPath(String path);

	Integer getIntegerForPath(String path);

	Double getDoubleForPath(String path);

	String getStringForPath(String path);

}
