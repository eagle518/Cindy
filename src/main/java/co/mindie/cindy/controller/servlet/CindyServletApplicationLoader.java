/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// ControllerLoader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 3, 2014 at 12:10:35 PM
////////

package co.mindie.cindy.controller.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;

import co.mindie.cindy.CindyWebApp;
import co.mindie.cindy.CindyWebAppCreator;
import org.apache.log4j.Logger;

import co.mindie.cindy.component.ComponentMetadataManager;

public abstract class CindyServletApplicationLoader implements ServletContextListener {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(CindyWebApp.class);
	private CindyWebApp application;
	private boolean shouldPreloadEndpoints;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CindyServletApplicationLoader() {
		this.shouldPreloadEndpoints = true;
	}

	////////////////////////
	// METHODS
	////////////////

	abstract protected CindyWebAppCreator getAppCreator();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		this.application = this.getAppCreator().createApplication();

		Dynamic dynamic = sce.getServletContext().addServlet("CindyServlet", new ServletAdapter(this.application.getControllerManager()));
		dynamic.addMapping("/*");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		LOGGER.info("Closing CindyWebApp...");
		if (this.application != null) {
			this.application.close();
		}
		LOGGER.info("CindyWebApp closed!");
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public boolean isShouldPreloadEndpoints() {
		return shouldPreloadEndpoints;
	}

	public void setShouldPreloadEndpoints(boolean shouldPreloadEndpoints) {
		this.shouldPreloadEndpoints = shouldPreloadEndpoints;
	}
}
