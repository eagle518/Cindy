package co.mindie.cindy.dao.impl;

import co.mindie.cindy.automapping.*;
import co.mindie.cindy.component.ComponentInitializer;
import co.mindie.cindy.component.ComponentMetadata;
import co.mindie.cindy.component.WireListener;
import co.mindie.cindy.dao.domain.OffsetedRequest;
import co.mindie.cindy.utils.Initializable;
import org.apache.log4j.Logger;

import java.util.List;

@Singleton
@Box
public class HibernateDAOValidator implements Initializable, WireListener {

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

	@Override
	public void onWillWire(ComponentInitializer initializer) {
		ComponentMetadata myMetadata = initializer.getMetadataManager().loadComponent(this.getClass());

		List<ComponentMetadata> daoMetadatas = initializer.getMetadataManager().findCompatibleComponentForClass(HibernateDAO.class);

		for (ComponentMetadata metadata : daoMetadatas) {
			myMetadata.addDependency(metadata.getComponentClass(), true, false, SearchScope.NO_SEARCH, CreationBox.CURRENT_BOX);
		}
	}

	@Override
	public void onWired(ComponentInitializer initializer) {

	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
