/////////////////////////////////////////////////
// Project : Mindie WebService
// Package : co.mindie.databases
// Database.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 29, 2014 at 7:24:27 PM
////////

package co.mindie.cindy.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.configuration.Configuration;
import co.mindie.cindy.database.tools.TracedHibernateDatabaseHandle;
import co.mindie.cindy.exception.ConfigurationException;
import co.mindie.cindy.utils.Pausable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.stat.Statistics;
import org.joda.time.DateTime;

import co.mindie.cindy.database.handle.HibernateDatabaseHandle;

public class HibernateDatabase extends Database implements Pausable {

	// //////////////////////
	// VARIABLES
	// //////////////

	private static final Logger LOGGER = Logger.getLogger(HibernateDatabase.class);
	@Wired
	private Configuration configuration;
	private SessionFactory sessionFactory;
	private String jdbcConnectionString;
	private boolean showDebug;
	private boolean traceStartedSession;
	private Map<HibernateDatabaseHandle, TracedHibernateDatabaseHandle> tracedHandles;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public HibernateDatabase() {
		this.tracedHandles = new HashMap<HibernateDatabaseHandle, TracedHibernateDatabaseHandle>();
	}

	// //////////////////////
	// METHODS
	// //////////////

	public void onOpenedSession(HibernateDatabaseHandle handle) {
		if (this.traceStartedSession) {
			StackTraceElement[] stackTrace =  new Throwable().getStackTrace();
			stackTrace = ArrayUtils.subarray(stackTrace, 1, stackTrace.length);
			TracedHibernateDatabaseHandle tracedHibernateDatabaseHandle = new TracedHibernateDatabaseHandle(handle, stackTrace);
			synchronized (this.tracedHandles) {
				this.tracedHandles.put(handle, tracedHibernateDatabaseHandle);
			}
		}
	}

	public void onClosedSession(HibernateDatabaseHandle handle) {
		if (this.traceStartedSession || !this.tracedHandles.isEmpty()) {
			synchronized (this.tracedHandles) {
				this.tracedHandles.remove(handle);
			}
		}

		this.signalDidCloseDatabaseSession();
	}

	@Override
	public void init() {
		super.init();

		Boolean traceStartedSession = this.configuration.getBoolean("wsframework.hibernate_trace_sessions");

		if (traceStartedSession == null) {
			traceStartedSession = this.configuration.getBoolean("cindy.hibernate_trace_sessions", false);
		}

		this.traceStartedSession = traceStartedSession;
	}

	public long getTotalQueryCount() {
		Statistics stats = this.getHibernateStatistics();

		return stats.getEntityDeleteCount() + stats.getEntityFetchCount() + stats.getEntityInsertCount() + stats.getEntityUpdateCount() +
				stats.getQueryExecutionCount() + stats.getCollectionFetchCount();
	}

	public SessionFactory getSessionFactory() {
		SessionFactory sessionFactory = this.sessionFactory;
		if (sessionFactory == null) {
			synchronized (this) {
				if (this.sessionFactory == null) {
					final org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

					LOGGER.trace("Starting HibernateDatabase with JDBC_CONNECTION_STRING=" + this.jdbcConnectionString);
					if (this.jdbcConnectionString != null) {
						configuration.getProperties().setProperty("hibernate.connection.url", this.jdbcConnectionString);
						if (this.jdbcConnectionString.startsWith("jdbc:mysql:")) {
							configuration.getProperties().setProperty("hibernate.show_sql", "false");
							configuration.getProperties().setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
							configuration.getProperties().setProperty("hibernate.dialect", "CindyMySQLDialect");
							LOGGER.trace("Setting up a MySQL database");
						} else if (this.jdbcConnectionString.startsWith("jdbc:h2:")) {
							configuration.getProperties().setProperty("hibernate.show_sql", this.showDebug ? "true" : "false");
							configuration.getProperties().setProperty("hibernate.connection.driver_class", "org.h2.Driver");
							configuration.getProperties().setProperty("hibernate.dialect", "CindyH2Dialect");
							configuration.getProperties().setProperty("hibernate.hbm2ddl.auto", "update");
							LOGGER.trace("Setting up a H2 database");
						} else {
							throw new ConfigurationException("Unknown JDBC database type: " + this.jdbcConnectionString);
						}
					} else {
						throw new ConfigurationException("Null JDBC connection string");
					}

					configuration.configure();

					ServiceRegistryBuilder builder = new ServiceRegistryBuilder();
					builder.applySettings(configuration.getProperties());

					this.sessionFactory = configuration.buildSessionFactory(builder.buildServiceRegistry());
					return this.sessionFactory;
				}

				sessionFactory = this.sessionFactory;
			}
		}

		return sessionFactory;
	}

	public List<String> getTablesNames() {
		SessionFactory factory = this.getSessionFactory();
		Map<String, ClassMetadata> map = factory.getAllClassMetadata();
		List<String> names = new ArrayList<>(map.size());
		names.addAll(map.values().stream().map(step -> ((SingleTableEntityPersister) step).getTableName()).collect(Collectors.toList()));
		return names;
	}

	public Statistics getHibernateStatistics() {
		Statistics stats = this.getSessionFactory().getStatistics();

		stats.setStatisticsEnabled(true);

		return stats;
	}

	public Session openSession() {
		this.signalWillOpenDatabaseSession();

		return this.getSessionFactory().openSession();
	}

	@Override
	public void close() {
		synchronized (this) {
			if (this.sessionFactory != null) {
				this.sessionFactory.close();
				this.sessionFactory = null;
			}
		}
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	public boolean isShowDebug() {
		return this.showDebug;
	}

	public void setShowDebug(boolean showDebug) {
		this.showDebug = showDebug;
	}

	public String getJdbcConnectionString() {
		return this.jdbcConnectionString;
	}

	public void setJdbcConnectionString(String jdbcConnectionString) {
		this.jdbcConnectionString = jdbcConnectionString;
	}

	public List<TracedHibernateDatabaseHandle> getHandlesWithActiveSessions() {
		List<TracedHibernateDatabaseHandle> handles = new ArrayList<>();

		synchronized (this.tracedHandles) {
			handles.addAll(this.tracedHandles.values());
		}

		return handles;
	}

	public List<TracedHibernateDatabaseHandle> getHandlesWithActiveSessionsLivingFor(int minutes) {
		List<TracedHibernateDatabaseHandle> handles = new ArrayList<>();
		DateTime threshold = DateTime.now().minusMinutes(minutes);

		synchronized (this.tracedHandles) {
			for (TracedHibernateDatabaseHandle handle : this.tracedHandles.values()) {
				if (handle.getCreatedDate().isBefore(threshold)) {
					handles.add(handle);
				}
			}
		}

		return handles;
	}

	public boolean isTracingStartedSession() {
		return this.traceStartedSession;
	}
}