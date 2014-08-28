package co.mindie.cindy.dao.utils;

import co.mindie.cindy.exception.ConfigurationException;
import org.apache.log4j.Logger;
import org.reflections.Reflections;

import javax.persistence.Entity;

/**
 * Utility class that extends @{link org.hibernate.cfg.Configuration} to add:
 * - default configuration for H2 databases
 * - default configuration for MySQL databases
 * - a method to scan a package, looking for annotated classes with @{link javax.persistence.Entity}
 * <p/,
 * The value of the property "hibernate.show_sql" is defined by the static variable DEFAULT_SHOW_SQL
 */
public class CindyHibernateConfiguration extends org.hibernate.cfg.Configuration {
	// //////////////////////
	// VARIABLES
	// //////////////

	private static final Logger LOGGER = Logger.getLogger(CindyHibernateConfiguration.class);
	public static boolean DEFAULT_SHOW_SQL = true;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public CindyHibernateConfiguration(String jdbcUrl) {
		if (jdbcUrl == null) {
			throw new ConfigurationException("The JDBCUrl is null.");
		}

		this.configureCommon(jdbcUrl);

		if (jdbcUrl.startsWith("jdbc:mysql:")) {
			this.configureForMySQL();
		} else if (jdbcUrl.startsWith("jdbc:h2:")) {
			this.configureForH2();
		} else {
			LOGGER.warn("The database type is unknown. You may review the configuration yourself.");
		}
	}

	// //////////////////////
	// METHODS
	// //////////////

	public void configureCommon(String jdbcUrl) {
		this.getProperties().setProperty("hibernate.connection.url", jdbcUrl);
		this.getProperties().setProperty("hibernate.show_sql", Boolean.toString(DEFAULT_SHOW_SQL));

		// TODO move that in Mindie?
		this.getProperties().setProperty("hibernate.current_session_context_class", "thread");
		this.getProperties().setProperty("hibernate.jdbc.batch_size", "40");
		this.getProperties().setProperty("hibernate.generate_statistics", "false");

		// TODO move that in Mindie and move the c3p0 dependency in Mindie?
		this.getProperties().setProperty("hibernate.c3p0.min_size", "10");
		this.getProperties().setProperty("hibernate.c3p0.max_size", "50");
		this.getProperties().setProperty("hibernate.c3p0.timeout", "300");
		this.getProperties().setProperty("hibernate.c3p0.max_statements", "0");
		this.getProperties().setProperty("hibernate.c3p0.idle_test_period", "100");
		this.getProperties().setProperty("hibernate.c3p0.num_helper_threads", "10");
	}

	public void configureForMySQL() {
		this.getProperties().setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		this.getProperties().setProperty("hibernate.dialect", "co.mindie.cindy.database.dialect.CindyMySQLDialect");
		LOGGER.trace("Setting up a MySQL database");
	}

	public void configureForH2() {
		this.getProperties().setProperty("hibernate.connection.driver_class", "org.h2.Driver");
		this.getProperties().setProperty("hibernate.dialect", "co.mindie.cindy.database.dialect.CindyH2Dialect");
		this.getProperties().setProperty("hibernate.hbm2ddl.auto", "update");
		this.scanPackageForAnnotatedClasses(""); // Scan the world
		LOGGER.trace("Setting up a H2 database");
	}

	public CindyHibernateConfiguration scanPackageForAnnotatedClasses(String packagePath) {
		LOGGER.trace("Scanning package " + packagePath + " for entities.");
		new Reflections(packagePath)
				.getTypesAnnotatedWith(Entity.class)
				.forEach(cls -> {
					LOGGER.trace("\tFound entity class: " + cls);
					this.addAnnotatedClass(cls);
				});
		return this;
	}
}
