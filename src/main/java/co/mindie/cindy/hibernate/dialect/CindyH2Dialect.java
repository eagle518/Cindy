/////////////////////////////////////////////////
// Project : Mindie WebService
// Package : com.ever.webservice.utils
// MindieMySQLDialect.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 28, 2013 at 1:31:58 PM
////////

package co.mindie.cindy.hibernate.dialect;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;

public class CindyH2Dialect extends H2Dialect {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CindyH2Dialect() {
		super();

		this.registerFunction("isnull", new StandardSQLFunction("isnull"));
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public String getDropSequenceString(String sequenceName) {
		// Adding the "if exists" clause to avoid warnings
		return "drop sequence if exists " + sequenceName;
	}

	@Override
	public boolean dropConstraints() {
		// We don't need to drop constraints before dropping tables, that just
		// leads to error messages about missing tables when we don't have a
		// schema in the database
		return false;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
