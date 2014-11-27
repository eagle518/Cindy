package co.mindie.cindy.hibernate.utils;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;

import javax.persistence.Entity;

/**
 * This tool provides an easy way to scan a package, looking for classes annotated with @Entity
 */
public class HibernateModelScanner {
	// //////////////////////
	// VARIABLES
	// //////////////

	private static final Logger LOGGER = Logger.getLogger(HibernateModelScanner.class);

	// //////////////////////
	// METHODS
	// //////////////

	public static void scanAnnotatedModel(Configuration configuration, String packagePath) {
		LOGGER.trace("Scanning package " + packagePath + " for entities.");
		new Reflections(packagePath)
				.getTypesAnnotatedWith(Entity.class)
				.forEach(cls -> {
					LOGGER.trace("\tFound entity class: " + cls);
					configuration.addAnnotatedClass(cls);
				});
	}
}
