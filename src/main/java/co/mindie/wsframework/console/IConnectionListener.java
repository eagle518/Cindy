/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.console
// IConnectionListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 30, 2014 at 3:04:06 PM
////////

package co.mindie.wsframework.console;

public interface IConnectionListener {

	void onDisconnected(Connection connection, Exception e);

}
