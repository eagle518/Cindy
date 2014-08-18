/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// ControllerLoader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 3, 2014 at 12:10:35 PM
////////

package co.mindie.cindy.controller;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;

import co.mindie.cindy.CindyApp;
import co.mindie.cindy.component.ComponentContext;
import co.mindie.cindy.exception.CindyException;
import org.apache.log4j.Logger;

import co.mindie.cindy.component.ComponentInitializer;
import co.mindie.cindy.component.ComponentMetadataManager;
import co.mindie.cindy.controller.servlet.ServletAdapter;

public abstract class CindyApplicationLoader implements ServletContextListener {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(CindyApp.class);
	private CindyApp application;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	protected CindyApp createApplication(ComponentMetadataManager componentMetadataManager) {
		return new CindyApp(componentMetadataManager);
	}

	abstract protected void configureApplication(ComponentInitializer initializer, CindyApp application);

	abstract protected void onApplicationInitialized(CindyApp application);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ComponentMetadataManager metadataManager = new ComponentMetadataManager();
		ComponentInitializer initializer = metadataManager.createInitializer();

		this.application = this.createApplication(metadataManager);

		if (this.application == null) {
			throw new CindyException("ControllerLoader must create a CindyApp instance");
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
		LOGGER.info("Closing CindyApp...");
		if (this.application != null) {
			this.application.close();
		}
		LOGGER.info("CindyApp closed!");
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
