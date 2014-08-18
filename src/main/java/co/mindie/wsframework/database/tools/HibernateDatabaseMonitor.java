/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.database
// HibernateDatabaseMonitor.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 7, 2014 at 10:56:03 AM
////////

package co.mindie.wsframework.database.tools;

import co.mindie.wsframework.automapping.Wired;
import co.mindie.wsframework.component.WSComponent;
import co.mindie.wsframework.configuration.WSConfiguration;
import co.mindie.wsframework.database.HibernateDatabase;
import co.mindie.wsframework.database.handle.HibernateDatabaseHandle;
import me.corsin.javatools.timer.TimeSpan;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.Closeable;
import java.util.List;

public abstract class HibernateDatabaseMonitor extends WSComponent implements Runnable, Closeable {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(HibernateDatabaseMonitor.class);
	@Wired private HibernateDatabase database;
	@Wired private WSConfiguration configuration;
	private boolean cont;
	private int leakDetectThredshold;

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	@Override
	public void init() {
		super.init();

		this.leakDetectThredshold = this.configuration.getInteger("wsframework.hibernate_session_leak_detect_threshold", 5);
		if (this.configuration.getBoolean("wsframework.hibernate_trace_sessions", false)) {
			this.cont = true;

			new Thread(this).start();
		}
	}

	@Override
	public void run() {
		try {
			while (this.cont) {
				this.detectLeaks();
				Thread.sleep(TimeSpan.fromMinutes(1).getTotalMs());
			}
		} catch (InterruptedException ignored) {
		}
	}

	public void detectLeaks() {
		LOGGER.trace("DetectLeaks() launched.");
		List<TracedHibernateDatabaseHandle> leakedHandles = this.database.getHandlesWithActiveSessionsLivingFor(this.leakDetectThredshold);

		for (TracedHibernateDatabaseHandle handle : leakedHandles) {
			this.onDetectedLeakedDatabaseHandle(handle.getHandle(), handle.getCreatedDate(), handle.getStackTrace());
		}
	}

	@Override
	public void close() {
		this.cont = false;
	}

	protected abstract void onDetectedLeakedDatabaseHandle(HibernateDatabaseHandle handle, DateTime createdDate, StackTraceElement[] stackTrace);

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
