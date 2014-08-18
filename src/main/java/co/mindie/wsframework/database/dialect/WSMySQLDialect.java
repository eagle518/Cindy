/////////////////////////////////////////////////
// Project : Mindie WebService
// Package : com.ever.webservice.utils
// MindieMySQLDialect.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 28, 2013 at 1:31:58 PM
////////

package co.mindie.wsframework.database.dialect;

import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.function.StandardSQLFunction;

public class WSMySQLDialect extends MySQL5InnoDBDialect {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public WSMySQLDialect() {
		super();

		this.registerFunction("isnull", new StandardSQLFunction("isnull"));
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
