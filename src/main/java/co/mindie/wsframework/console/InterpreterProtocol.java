/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.console
// ConsoleProtocol.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 1, 2014 at 12:27:28 PM
////////

package co.mindie.wsframework.console;

import java.io.IOException;

import me.corsin.javatools.dynamictext.Context;
import me.corsin.javatools.dynamictext.ContextObjectResolver;
import me.corsin.javatools.string.Strings;

public class InterpreterProtocol implements IConsoleProtocol {

	////////////////////////
	// VARIABLES
	////////////////

	private Context context;
	private Connection connection;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public InterpreterProtocol(Connection connection) {
		this.context = new Context();
		this.context.put("Protocol", this);
		this.context.put("App", connection.getApplication());
		this.context.put("Class", new ConsoleTools.Class(this));
		this.context.put("Collection", new ConsoleTools.Collection(this));
		this.context.put("Component", new ConsoleTools.Component(this));
		this.context.put("help", new ConsoleTools.Help(this));
		this.connection = connection;
	}

	////////////////////////
	// METHODS
	////////////////

	private boolean tryAssignment(Connection connection, String command) throws IOException {
		String[] assigments = command.split("=");

		if (assigments.length == 2) {
			String variable = assigments[0].trim();
			String valueStr = assigments[1].trim();

			ContextObjectResolver objectResolver = new ContextObjectResolver(valueStr);
			Object value = objectResolver.resolveForObjectAndContext(null, this.context);

			this.context.put(variable, value);

			connection.send(variable + " = " + (value != null ? value.toString() : "null") + "\n");

			return true;
		}

		return false;
	}

	@Override
	public void handleCommand(Connection connection, String command) {
		command = command.trim();

		if (!command.isEmpty()) {
			try {
				if (!this.tryAssignment(connection, command)) {
					ContextObjectResolver resolver = new ContextObjectResolver(command);
					Object result = resolver.resolveForObjectAndContext(null, this.context);
					String resultStr = Strings.getObjectDescription(result);
					connection.send(resultStr + "\n");
				}
			} catch (Exception e) {
				connection.send(Strings.format("{#0}: {#1}\n", e.getClass().getSimpleName(), e.getMessage()));
			}

		}
		this.prompt();
	}

	public void setLogLevel(String levelStr) {
		this.connection.setLogLevel(levelStr);
	}

	private void prompt() {
		this.connection.send("$> ");
	}

	@Override
	public void onConnectionBecameReady(Connection connection) {
		String welcome = ""
				+ "=======================================================\n"
				+ "          Welcome in the console interpreter\n"
				+ "          Get some help by typing \"help\"!\n"
				+ "=======================================================\n"
				+ ""
				+ "";

		connection.send(welcome);
		this.prompt();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Connection getConnection() {
		return this.connection;
	}

	public Context getContext() {
		return this.context;
	}

}
