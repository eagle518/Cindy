/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// ControllerLoader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 3, 2014 at 12:10:35 PM
////////

package co.mindie.wsframework.controller;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;

import org.apache.log4j.Logger;

import co.mindie.wsframework.WSApplication;
import co.mindie.wsframework.component.ComponentContext;
import co.mindie.wsframework.component.ComponentInitializer;
import co.mindie.wsframework.component.ComponentMetadataManager;
import co.mindie.wsframework.controller.servlet.ServletAdapter;
import co.mindie.wsframework.misc.WSFrameworkException;

public abstract class WSApplicationLoader implements ServletContextListener {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(WSApplication.class);
	private WSApplication application;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	protected WSApplication createApplication(ComponentMetadataManager componentMetadataManager) {
		return new WSApplication(componentMetadataManager);
	}

	abstract protected void configureApplication(ComponentInitializer initializer, WSApplication application);

	abstract protected void onApplicationInitialized(WSApplication application);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ComponentMetadataManager metadataManager = new ComponentMetadataManager();
		ComponentInitializer initializer = metadataManager.createInitializer();

		this.application = this.createApplication(metadataManager);

		if (this.application == null) {
			throw new WSFrameworkException("ControllerLoader must create a WSApplication instance");
		}

		initializer.addCreatedComponent(this.application, new ComponentContext());

		this.configureApplication(initializer, this.application);

		initializer.init();

		Dynamic dynamic = sce.getServletContext().addServlet("WSServlet", new ServletAdapter(this.application.getControllerManager()));
		dynamic.addMapping("/*");

		this.onApplicationInitialized(this.application);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		LOGGER.info("Closing WSApplication...");
		if (this.application != null) {
			this.application.close();
		}
		LOGGER.info("WSApplication closed!");
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
