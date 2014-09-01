/////////////////////////////////////////////////
// Project : Mindie WebService
// Package : co.mindie.databases
// Database.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 29, 2014 at 7:24:27 PM
////////

package co.mindie.cindy.database;

import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.configuration.Configuration;
import co.mindie.cindy.database.handle.HibernateDatabaseHandle;
import co.mindie.cindy.database.tools.TracedHibernateDatabaseHandle;
import co.mindie.cindy.utils.Pausable;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.stat.Statistics;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class HibernateDatabase extends Database implements Pausable {

	// //////////////////////
	// VARIABLES
	// //////////////

	@Wired private Configuration configuration;
	private SessionFactory sessionFactory;
	private boolean traceStartedSession;
	private final Map<HibernateDatabaseHandle, TracedHibernateDatabaseHandle> tracedHandles;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public HibernateDatabase() {
		this.tracedHandles = new HashMap<>();
	}

	// //////////////////////
	// METHODS
	// //////////////

	public void onOpenedSession(HibernateDatabaseHandle handle) {
		if (this.traceStartedSession) {
			StackTraceElement[] stackTrace = new Throwable().getStackTrace();
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
					final org.hibernate.cfg.Configuration configuration = this.getHibernateConfiguration();
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

	protected abstract org.hibernate.cfg.Configuration getHibernateConfiguration();

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
			handles.addAll(this.tracedHandles.values().stream()
					.filter(handle -> handle.getCreatedDate().isBefore(threshold))
					.collect(Collectors.toList()));
		}

		return handles;
	}

	public boolean isTracingStartedSession() {
		return this.traceStartedSession;
	}
}