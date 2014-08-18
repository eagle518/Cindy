/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.context
// ListProperties.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 7, 2014 at 4:15:29 PM
////////

package co.mindie.cindy.context;

import org.joda.time.DateTime;

public class ListProperties {

	////////////////////////
	// VARIABLES
	////////////////

	public int offset;
	public int limit;
	public DateTime beforeDate;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ListProperties(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
	}

	public ListProperties(int offset, int limit, DateTime beforeDate) {
		this.offset = offset;
		this.limit = limit;
		this.beforeDate = beforeDate;
	}

	public ListProperties() {

	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
