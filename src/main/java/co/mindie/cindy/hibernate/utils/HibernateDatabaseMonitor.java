/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.database
// HibernateDatabaseMonitor.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 7, 2014 at 10:56:03 AM
////////

package co.mindie.cindy.hibernate.utils;

import co.mindie.cindy.core.annotation.Wired;
import co.mindie.cindy.webservice.configuration.Configuration;
import co.mindie.cindy.hibernate.database.HibernateDatabase;
import co.mindie.cindy.hibernate.database.handle.HibernateDatabaseHandle;
import co.mindie.cindy.core.tools.Initializable;
import me.corsin.javatools.timer.TimeSpan;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.Closeable;
import java.util.List;

public abstract class HibernateDatabaseMonitor implements Runnable, Closeable, Initializable {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(HibernateDatabaseMonitor.class);
	@Wired private Configuration configuration;
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
		// TODO remove this reference to WSFramework
		Integer leakDetectThredshold = this.configuration.getInteger("wsframework.hibernate_session_leak_detect_threshold");

		if (leakDetectThredshold == null) {
			leakDetectThredshold = this.configuration.getInteger("cindy.hibernate_session_leak_detect_threshold", 5);
		}

		this.leakDetectThredshold = leakDetectThredshold;

		// TODO remove this reference to WSFramework
		Boolean traceSession = this.configuration.getBoolean("wsframework.hibernate_trace_sessions");

		if (traceSession == null) {
			traceSession = this.configuration.getBoolean("cindy.hibernate_trace_sessions", false);
		}

		if (traceSession) {
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
		List<TracedHibernateDatabaseHandle> leakedHandles = this.getHibernateDatabase().getHandlesWithActiveSessionsLivingFor(this.leakDetectThredshold);

		for (TracedHibernateDatabaseHandle handle : leakedHandles) {
			this.onDetectedLeakedDatabaseHandle(handle.getHandle(), handle.getCreatedDate(), handle.getStackTrace());
		}
	}

	@Override
	public void close() {
		this.cont = false;
	}

	protected abstract void onDetectedLeakedDatabaseHandle(HibernateDatabaseHandle handle, DateTime createdDate, StackTraceElement[] stackTrace);

	public abstract HibernateDatabase getHibernateDatabase();

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
