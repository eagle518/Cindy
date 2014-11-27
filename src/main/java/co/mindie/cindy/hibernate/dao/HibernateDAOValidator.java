package co.mindie.cindy.hibernate.dao;

import co.mindie.cindy.core.annotation.Box;
import co.mindie.cindy.core.annotation.MetadataModifier;
import co.mindie.cindy.core.annotation.Wired;
import co.mindie.cindy.core.component.metadata.ComponentMetadata;
import co.mindie.cindy.core.component.metadata.ComponentMetadataManagerBuilder;
import co.mindie.cindy.core.component.CreationBox;
import co.mindie.cindy.core.component.SearchScope;
import co.mindie.cindy.dao.domain.OffsetedRequest;
import co.mindie.cindy.core.tools.Initializable;
import co.mindie.cindy.webservice.annotation.Singleton;
import org.apache.log4j.Logger;

import java.util.List;

@Singleton
@Box
public class HibernateDAOValidator implements Initializable {

	////////////////////////
	// VARIABLES
	////////////////

	private static Logger LOGGER = Logger.getLogger(HibernateDAOValidator.class);

	@Wired
	private List<HibernateDAO> hibernateDAOs;

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	@Override
	public void init() {
		LOGGER.info("Validating all Hibernate DAOs");

		for (HibernateDAO hibernateDAO : hibernateDAOs) {
			LOGGER.info("Validating DAO " + hibernateDAO.getClass().getSimpleName());
			hibernateDAO.findAll(new OffsetedRequest(0, 1));
			hibernateDAO.close();
		}

		LOGGER.info("Successfully validated all Hibernate DAOs");
	}

	@MetadataModifier
	public static void autoAddDependencies(ComponentMetadataManagerBuilder componentMetadataManagerBuilder) {
		for (ComponentMetadata myMetadata : componentMetadataManagerBuilder.findCompatibleComponentsForClass(HibernateDAOValidator.class)) {
			List<ComponentMetadata> daoMetadatas = componentMetadataManagerBuilder.findCompatibleComponentsForClass(HibernateDAO.class);

			if (daoMetadatas != null) {
				for (ComponentMetadata metadata : daoMetadatas) {
					myMetadata.addDependency(metadata.getComponentClass(), true, false, SearchScope.NO_SEARCH, CreationBox.CURRENT_BOX);
				}
			}
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
