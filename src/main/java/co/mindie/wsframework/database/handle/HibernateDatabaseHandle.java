/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.context
// SessionHandle.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 29, 2014 at 7:01:46 PM
////////

package co.mindie.wsframework.database.handle;

import java.io.Serializable;

import me.corsin.javatools.exception.StackTraceUtils;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.LockAcquisitionException;

import co.mindie.wsframework.component.ComponentContext;
import co.mindie.wsframework.component.WSComponent;
import co.mindie.wsframework.database.HibernateDatabase;
import co.mindie.wsframework.database.WSDatabase;
import co.mindie.wsframework.utils.IFlushable;

public abstract class HibernateDatabaseHandle extends WSComponent implements IDatabaseHandle, IFlushable {

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

	@Override
	public void init() {
		super.init();
		LOGGER.trace("Initialized HibernateDatabaseHandle in " + printComponentContextTree(this.getComponentContext(), true));
	}

	private static String printComponentContextTree(ComponentContext ctx, boolean first) {
		String prefix = first ? "" : " < ";
		String parents = "";
		if (ctx.getParentContext() != null) {
			parents = printComponentContextTree(ctx.getParentContext(), false);
		}
		return prefix + ctx + parents;
	}

	@Override
	public void close() {
		LOGGER.trace("Closing HibernateDatabaseHandle in " + printComponentContextTree(this.getComponentContext(), true));
		this.autoFlushEnabled = false;

		try {
			this.flush();
		} finally {
			this.sessionThread = null;
			if (this.openedSession != null) {
				try {
					this.openedSession.close();
				} finally {
					this.openedSession = null;
					this.getHibernateDatabase().onClosedSession(this);
				}
			}
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
			} catch (HibernateException e) {
				e.printStackTrace();
			}
			this.startedTransaction = null;
		}
	}

	public Session createSession() {
		LOGGER.trace("HibernateDatabaseHandle opens a session in " + printComponentContextTree(this.getComponentContext(), true));
		return this.getHibernateDatabase().openSession();
	}

	private void ensureValidSessionUsage() {
		if (this.sessionThread != Thread.currentThread()) {
			throw new InvalidSessionUsageException(this.sessionThreadStackTrace);
		}
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
			this.sessionThread = Thread.currentThread();
			this.sessionThreadStackTrace = StackTraceUtils.stackTraceToString(new Throwable());
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

	abstract public HibernateDatabase getHibernateDatabase();

	@Override
	public WSDatabase getDatabase() {
		return this.getHibernateDatabase();
	}
}
