package co.mindie.cindy.database.tools;

import co.mindie.cindy.exception.ConfigurationException;
import org.apache.log4j.Logger;

/**
 * Utility class that extends @{link org.hibernate.cfg.Configuration} to add:
 * - default configuration for H2 databases
 * - default configuration for MySQL databases
 * - a method to scan a package, looking for annotated classes with @{link javax.persistence.Entity}
 * <p/>
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

	public CindyHibernateConfiguration(String jdbcUrl, Boolean showSQL) {
		if (showSQL == null) {
			showSQL = DEFAULT_SHOW_SQL;
		}
		if (jdbcUrl == null) {
			throw new ConfigurationException("The JDBCUrl is null.");
		}

		this.configureCommon(jdbcUrl, showSQL);

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

	private void configureCommon(String jdbcUrl, boolean showSQL) {
		this.getProperties().setProperty("hibernate.connection.url", jdbcUrl);
		this.getProperties().setProperty("hibernate.show_sql", Boolean.toString(showSQL));
	}

	private void configureForMySQL() {
		this.getProperties().setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		this.getProperties().setProperty("hibernate.dialect", "co.mindie.cindy.database.dialect.CindyMySQLDialect");
		LOGGER.trace("Setting up a MySQL database");
	}

	private void configureForH2() {
		this.getProperties().setProperty("hibernate.connection.driver_class", "org.h2.Driver");
		this.getProperties().setProperty("hibernate.dialect", "co.mindie.cindy.database.dialect.CindyH2Dialect");
		this.getProperties().setProperty("hibernate.hbm2ddl.auto", "update");
		LOGGER.trace("Setting up a H2 database");
	}

	public CindyHibernateConfiguration scanPackageForAnnotatedClasses(String packageName) {
		HibernateModelScanner.scanAnnotatedModel(this, packageName);
		return this;
	}
}
