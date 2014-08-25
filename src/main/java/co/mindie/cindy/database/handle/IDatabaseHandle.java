/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.context
// SessionHandle.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 29, 2014 at 7:01:46 PM
////////

package co.mindie.cindy.database.handle;

import java.io.Closeable;

import co.mindie.cindy.database.Database;

public interface IDatabaseHandle extends Closeable {

	Database getDatabase();

	@Override
	void close();

}