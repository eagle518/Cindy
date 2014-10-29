/////////////////////////////////////////////////
// Project : WSFramework
// Package :
// CindyWebApp.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 3, 2014 at 6:22:22 PM
////////

package co.mindie.cindy;

import co.mindie.cindy.automapping.*;
import co.mindie.cindy.component.*;
import co.mindie.cindy.configuration.Configuration;
import co.mindie.cindy.controller.manager.ControllerManager;
import co.mindie.cindy.resolver.ResolverManager;
import co.mindie.cindy.utils.Pausable;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

@Load(creationPriority = -1)
@Component(aspects = { ComponentAspect.SINGLETON, ComponentAspect.THREAD_SAFE })
@Box(needAspects = { ComponentAspect.SINGLETON, ComponentAspect.THREAD_SAFE })
public class CindyWebApp implements Pausable, Closeable {

	// //////////////////////
	// VARIABLES
	// //////////////

	private boolean paused;

	@Wired private Configuration configuration;
	@Wired private ResolverManager resolver;
	@Wired private ControllerManager controllerManager;
	@Wired(required = false) private Appender rootLogAppender;
	@Wired private List<Pausable> pausables;

	@WiredCore private ComponentBox innerBox;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public CindyWebApp() {
		DateTimeZone.setDefault(DateTimeZone.UTC);
	}

	// //////////////////////
	// METHODS
	// //////////////

	public void init() {
		if (this.rootLogAppender != null) {
			Logger.getRootLogger().addAppender(this.rootLogAppender);
		}
	}

	@Override
	public void pause() {
		this.paused = true;
		for (Pausable pausable : this.pausables) {
			pausable.pause();
		}
	}

	@Override
	public void resume() {
		this.paused = false;
		for (Pausable pausable : this.pausables) {
			pausable.resume();
		}
	}

	@Override
	public void close() {
		this.innerBox.close();
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	public Configuration getConfiguration() {
		return this.configuration;
	}

	public ControllerManager getControllerManager() {
		return this.controllerManager;
	}

	public ResolverManager getResolverManager() {
		return this.resolver;
	}

	@Override
	public boolean isPaused() {
		return this.paused;
	}

}
