/////////////////////////////////////////////////
// Project : Mindie WebService
// Package : co.mindie.databases
// Database.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 29, 2014 at 7:24:27 PM
////////

package co.mindie.cindy.database;

import java.io.Closeable;

import co.mindie.cindy.component.CindyComponent;
import co.mindie.cindy.utils.Pausable;
import org.apache.log4j.Logger;

public abstract class Database extends CindyComponent implements Closeable, Pausable {

	// //////////////////////
	// VARIABLES
	// //////////////

	private static Logger LOGGER = Logger.getLogger(Database.class);
	private Object pauseLock;
	private Object sessionCountLock;
	private int sessionOpenCount;
	private int sessionCloseCount;
	private boolean paused;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public Database() {
		this.pauseLock = new Object();
		this.sessionCountLock = new Object();
	}

	// //////////////////////
	// METHODS
	// //////////////

	protected void signalWillOpenDatabaseSession() {
		if (this.paused) {
			synchronized (this.pauseLock) {
				if (this.paused) {
					try {
						this.pauseLock.wait();
					} catch (InterruptedException e) { }
				}
			}
		}

		synchronized (this.sessionCountLock) {
			this.sessionOpenCount++;
		}
	}

	protected void signalDidCloseDatabaseSession() {
		synchronized (this.sessionCountLock) {
			this.sessionCloseCount++;

			// Notify to listeners that every opened sessions are now closed
			if (this.sessionOpenCount == this.sessionCloseCount) {
				this.sessionCountLock.notifyAll();
			}
		}
	}

	@Override
	public void pause() {
		this.paused = true;

		synchronized (this.sessionCountLock) {
			if (this.sessionCloseCount != this.sessionOpenCount) {
				try {
					LOGGER.info("Pausing database. Waiting that all opened sessions close.");
					this.sessionCountLock.wait();
				} catch (InterruptedException e) { }
			}
		}
		LOGGER.info("Paused database.");
	}

	@Override
	public void resume() {
		synchronized (this.pauseLock) {
			this.pauseLock.notifyAll();
		}
		this.paused = false;
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	public int getSessionCloseCount() {
		return this.sessionCloseCount;
	}

	public int getSessionOpenCount() {
		return this.sessionOpenCount;
	}

	@Override
	public boolean isPaused() {
		return this.paused;
	}
}

