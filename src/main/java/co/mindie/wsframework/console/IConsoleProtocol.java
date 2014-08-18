/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.console
// IConsoleProtocol.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 1, 2014 at 4:31:12 PM
////////

package co.mindie.wsframework.console;

public interface IConsoleProtocol {

	void handleCommand(Connection connection, String command);

	void onConnectionBecameReady(Connection connection);

}
