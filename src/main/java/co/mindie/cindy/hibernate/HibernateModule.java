package co.mindie.cindy.hibernate;

import co.mindie.cindy.core.module.Module;
import co.mindie.cindy.hibernate.dao.HibernateDAOValidator;
import co.mindie.cindy.hibernate.database.handle.SimpleHibernateDatabaseHandle;
import me.corsin.javatools.array.ArrayUtils;

/**
 * Created by simoncorsin on 12/01/15.
 */
public class HibernateModule implements Module {

	////////////////////////
	// VARIABLES
	////////////////

	private HibernateModuleConfiguration hibernateModuleConfiguration;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public HibernateModule() {
		this.hibernateModuleConfiguration = new HibernateModuleConfiguration();
	}

	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public HibernateModuleConfiguration getConfiguration() {
		return this.hibernateModuleConfiguration;
	}

	@Override
	public Class<?>[] getComponentClasses() {
		Class<?>[] componentClasses = new Class<?>[] {SimpleHibernateDatabaseHandle.class };

		if (this.hibernateModuleConfiguration.isEnabled(HibernateModuleConfiguration.DAO_VALIDATOR_ENABLED)) {
			componentClasses = ArrayUtils.addItem(componentClasses, HibernateDAOValidator.class);
		}

		return componentClasses;
	}

	@Override
	public String[] getComponentsClasspaths() {
		return null;
	}

	@Override
	public Module[] getDependencies() {
		return new Module[0];
	}


}
