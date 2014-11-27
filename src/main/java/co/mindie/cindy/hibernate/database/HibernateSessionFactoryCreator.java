package co.mindie.cindy.hibernate.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateSessionFactoryCreator {

	////////////////////////
	// VARIABLES
	////////////////

	final private Configuration configuration;
	final private ServiceRegistry serviceRegistry;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public HibernateSessionFactoryCreator(Configuration configuration, ServiceRegistry serviceRegistry) {
		this.configuration = configuration;
		this.serviceRegistry = serviceRegistry;
	}

	////////////////////////
	// METHODS
	////////////////

	public SessionFactory createSessionFactory() {
		return this.configuration.buildSessionFactory(this.serviceRegistry);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
