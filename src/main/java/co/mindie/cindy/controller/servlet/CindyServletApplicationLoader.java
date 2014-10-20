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

import co.mindie.cindy.CindyApp;
import co.mindie.cindy.CindyAppCreator;
import org.apache.log4j.Logger;

import co.mindie.cindy.component.ComponentMetadataManager;

public abstract class CindyServletApplicationLoader implements ServletContextListener {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(CindyApp.class);
	private CindyApp application;
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

	protected CindyApp createApplication(ComponentMetadataManager componentMetadataManager) {
		return new CindyApp(componentMetadataManager);
	}

	abstract protected void configureApplication(CindyApp application);

	abstract protected void onApplicationInitialized(CindyApp application);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		this.application = new CindyAppCreator() {

			@Override
			protected void onLoad(CindyApp application) {
				super.onLoad(application);
				configureApplication(application);
			}

			@Override
			protected CindyApp onCreate(ComponentMetadataManager metadataManager) {
				return CindyServletApplicationLoader.this.createApplication(metadataManager);
			}

		}.createApplication(this.shouldPreloadEndpoints);

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

	public boolean isShouldPreloadEndpoints() {
		return shouldPreloadEndpoints;
	}

	public void setShouldPreloadEndpoints(boolean shouldPreloadEndpoints) {
		this.shouldPreloadEndpoints = shouldPreloadEndpoints;
	}
}
