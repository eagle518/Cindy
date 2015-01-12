/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// ControllerLoader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 3, 2014 at 12:10:35 PM
////////

package co.mindie.cindy.webservice.controller.servlet;

import co.mindie.cindy.webservice.app.CindyWebApp;
import co.mindie.cindy.webservice.app.CindyWebAppCreator;
import javassist.ClassPool;
import javassist.LoaderClassPath;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;

/**
 * Creates and register a CindyWebApp as a servlet. Implementing this class
 * requires to implement getAppCreator() that is responsible for returning a
 * CindyWebAppCreator that will later be used to create the CindyWebApp instance.
 */
public abstract class CindyServletWebAppLoader implements ServletContextListener {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(CindyWebApp.class);
	private CindyWebApp application;
	private boolean shouldPreloadEndpoints;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CindyServletWebAppLoader() {
		this.shouldPreloadEndpoints = true;
	}

	////////////////////////
	// METHODS
	////////////////

	abstract protected CindyWebAppCreator getAppCreator();

	abstract protected void onReady(CindyWebApp webApp);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		ClassPool.getDefault().insertClassPath(new LoaderClassPath(cl));

		this.application = this.getAppCreator().createApplication();

		Dynamic dynamic = sce.getServletContext().addServlet("CindyServlet", new ServletAdapter(this.application.getControllerManager()));
		dynamic.addMapping("/*");

		this.onReady(this.application);
		LOGGER.info("CindyWebApp running.");
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
