/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.context
// SessionHandle.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 29, 2014 at 7:01:46 PM
////////

package co.mindie.cindy.hibernate.database.handle;

import co.mindie.cindy.core.component.box.ComponentBox;
import co.mindie.cindy.hibernate.database.HibernateDatabase;
import co.mindie.cindy.core.tools.Cancelable;
import co.mindie.cindy.core.tools.Flushable;
import me.corsin.javatools.exception.StackTraceUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.LockAcquisitionException;

import java.io.Closeable;
import java.io.Serializable;

public abstract class HibernateDatabaseHandle implements Closeable, Flushable, Cancelable {

	// //////////////////////
	// VARIABLES
	// //////////////

	private static final Logger LOGGER = Logger.getLogger(HibernateDatabaseHandle.class);
	private Session openedSession;
	private Transaction startedTransaction;
	private boolean autoFlushEnabled;
	private Thread sessionThread;
	private String sessionThreadStackTrace;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	// //////////////////////
	// METHODS
	// //////////////

	@Override
	protected void finalize() throws Throwable {
		try {
			super.finalize();
		} catch (Throwable e) {
			this.close();
		}
	}

	private static String printComponentContextTree(ComponentBox ctx, boolean first) {
		String prefix = first ? "" : " < ";
		String parents = "";
		if (ctx.getSuperBox() != null) {
			parents = printComponentContextTree(ctx.getSuperBox(), false);
		}
		return prefix + ctx + parents;
	}

	public void close() {
		LOGGER.trace("Closing HibernateDatabaseHandle#" + this.hashCode() + " with session#" + (this.openedSession != null ? this.openedSession.hashCode() : null));
		this.autoFlushEnabled = false;

		RuntimeException thrownException = null;

		try {
			this.flush();
		} catch (RuntimeException e) {
			thrownException = e;
		} finally {
			this.sessionThread = null;
			if (this.openedSession != null) {
				try {
					this.openedSession.close();
				} catch (RuntimeException e) {
					if (thrownException == null) {
						thrownException = e;
					}
				} finally {
					this.openedSession = null;
					this.getHibernateDatabase().onClosedSession(this);
				}
			}
		}

		if (thrownException != null) {
			throw thrownException;
		}
	}

	@Override
	public void flush() {
		if (this.startedTransaction != null) {
			try {
				this.startedTransaction.commit();
			} catch (HibernateException e) {
				this.cancelTransaction();
				throw e;
			} finally {
				this.startedTransaction = null;
			}
		}
	}

	@Override
	public void cancel() {
		this.cancelTransaction();
	}

	public void clear() {
		if (this.openedSession != null) {
			this.openedSession.clear();
		}
	}

	public void flushAndClear() {
		this.flush();
		this.clear();
	}

	private Serializable doSaveInSession(Object data, int attemptCount) {
		try {
			try {
				this.beginTransaction();

				Serializable serializable = this.getSession().save(data);
				this.autoFlush();

				return serializable;
			} catch (LockAcquisitionException e) {
				if (attemptCount < 2) {
					return this.doSaveInSession(data, attemptCount + 1);
				}

				throw e;
			}

		} catch (HibernateException e) {
			this.cancelTransaction();
			throw e;
		}
	}

	public Serializable save(Object data) {
		return this.doSaveInSession(data, 0);
	}

	public void update(Object data) {
		try {
			this.beginTransaction();
			this.getSession().update(data);
			this.autoFlush();
		} catch (HibernateException e) {
			this.cancelTransaction();
			throw e;
		}
	}

	final private void autoFlush() {
		if (this.autoFlushEnabled) {
			this.flush();
		}
	}

	public void delete(Object data) {
		try {
			this.beginTransaction();
			this.getSession().delete(data);
			this.autoFlush();
		} catch (HibernateException e) {
			this.cancelTransaction();
			throw e;
		}
	}

	public void beginTransaction() {
		if (this.startedTransaction == null) {
			this.startedTransaction = this.getSession().beginTransaction();
		}
	}

	public void cancelTransaction() {
		if (this.startedTransaction != null) {
			try {
				this.startedTransaction.rollback();
			} catch (Throwable e) {
				LOGGER.warn("An exception occurred while canceling a transaction " + StackTraceUtils.stackTraceToString(e));
			}
			this.startedTransaction = null;
		}
	}

	public Session createSession() {
		Session session = this.getHibernateDatabase().openSession();
		LOGGER.trace("HibernateDatabaseHandle opens a session#" + session.hashCode());
		return session;
	}

	private void ensureValidSessionUsage() {
		if (this.sessionThread != null && this.sessionThread != Thread.currentThread()) {
			throw new InvalidSessionUsageException(this.sessionThreadStackTrace);
		}
	}

	public void acquireOwnership() {
		this.sessionThread = Thread.currentThread();
		this.sessionThreadStackTrace = StackTraceUtils.stackTraceToString(new Throwable());
	}

	public void releaseOwnership() {
		this.sessionThread = null;
		this.sessionThreadStackTrace = null;
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	public final Session getSession() {
		if (this.openedSession == null) {
			this.openedSession = this.createSession();

			if (this.openedSession == null) {
				throw new RuntimeException("The session creator must return an hibernate session");
			}
			this.acquireOwnership();
			this.getHibernateDatabase().onOpenedSession(this);
		} else {
			this.ensureValidSessionUsage();
		}

		return this.openedSession;
	}

	public boolean hasStartedTransaction() {
		return this.startedTransaction != null;
	}

	public boolean isAutoFlushEnabled() {
		return this.autoFlushEnabled;
	}

	public void setAutoFlushEnabled(boolean autoFlushEnabled) {
		this.autoFlushEnabled = autoFlushEnabled;
	}

	public boolean isSessionBegan() {
		return this.openedSession != null;
	}

	public abstract HibernateDatabase getHibernateDatabase();

}
