/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.console
// SocketSender.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 30, 2014 at 1:29:19 PM
////////

package co.mindie.wsframework.console;

import co.mindie.wsframework.WSApplication;
import com.google.common.collect.Lists;
import me.corsin.javatools.io.IOUtils;
import me.corsin.javatools.task.SingleThreadedTaskQueue;
import me.corsin.javatools.task.TaskQueue;
import org.apache.log4j.Level;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Connection implements Closeable, Runnable {

	////////////////////////
	// VARIABLES
	////////////////

	private static final List<Level> LEVELS = Lists.newArrayList(
			Level.TRACE,
			Level.DEBUG,
			Level.WARN,
			Level.INFO,
			Level.ERROR,
			Level.FATAL,
			Level.OFF,
			Level.ALL
	);

	private WSApplication application;
	private Socket socket;
	private boolean closed;
	private IConnectionListener listener;
	private IConsoleProtocol protocol;
	private TaskQueue writeQueue;
	private Level logLevel;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Connection(WSApplication application, Socket socket) {
		this.application = application;
		this.socket = socket;
		this.writeQueue = new SingleThreadedTaskQueue();
		this.logLevel = Level.OFF;

		Thread thread = new Thread(this);
		thread.start();
	}

	////////////////////////
	// METHODS
	////////////////

	public void send(String message) {
		this.writeQueue.executeAsync(() -> {
			try {
				OutputStream outputStream = this.socket.getOutputStream();
				outputStream.write(message.getBytes("UTF-8"));
			} catch (IOException ignored) {
			}
		});
	}

	public void send(String message, Level level) {
		if (level.isGreaterOrEqual(this.logLevel)) {
			this.send(message);
		}
	}

	public void markReady() {
		if (this.protocol != null) {
			this.protocol.onConnectionBecameReady(this);
		}
	}

	@Override
	public void close() {
		this.closed = true;
		IOUtils.closeStream(this.socket);
		this.socket = null;
		if (this.writeQueue != null) {
			this.writeQueue.close();
			this.writeQueue = null;
		}
	}

	@Override
	public void run() {
		Scanner scanner = null;
		try {
			scanner = new Scanner(this.socket.getInputStream());
			while (!this.closed) {
				String command = scanner.nextLine().trim();

				if (this.protocol != null) {
					this.protocol.handleCommand(this, command);
				}
			}
		} catch (Exception e) {
			IConnectionListener listener = this.listener;
			if (listener != null) {
				listener.onDisconnected(this, e);
			}
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	private static Level resolveLevel(String str) {
		for (Level level : LEVELS) {
			if (level.toString().equals(str)) {
				return level;
			}
		}
		List<String> levelsStr = LEVELS.stream().map(Level::toString).collect(Collectors.toList());
		throw new RuntimeException("Unknown level '" + str + "'. Possibilities are: " + levelsStr);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Socket getSocket() {
		return this.socket;
	}

	public IConnectionListener getListener() {
		return this.listener;
	}

	public void setListener(IConnectionListener listener) {
		this.listener = listener;
	}

	public IConsoleProtocol getProtocol() {
		return this.protocol;
	}

	public void setProtocol(IConsoleProtocol protocol) {
		this.protocol = protocol;
	}

	public WSApplication getApplication() {
		return this.application;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = resolveLevel(logLevel);
	}

	public Level getLogLevel() {
		return this.logLevel;
	}
}
