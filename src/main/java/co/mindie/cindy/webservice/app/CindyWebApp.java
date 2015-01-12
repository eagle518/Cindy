/////////////////////////////////////////////////
// Project : WSFramework
// Package :
// CindyWebApp.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 3, 2014 at 6:22:22 PM
////////

package co.mindie.cindy.webservice.app;

import co.mindie.cindy.core.annotation.*;
import co.mindie.cindy.core.component.CreationBox;
import co.mindie.cindy.core.component.SearchScope;
import co.mindie.cindy.core.component.metadata.ComponentMetadata;
import co.mindie.cindy.core.component.metadata.ComponentMetadataManagerBuilder;
import co.mindie.cindy.webservice.annotation.Singleton;
import co.mindie.cindy.core.component.*;
import co.mindie.cindy.core.component.box.ComponentBox;
import co.mindie.cindy.webservice.configuration.Configuration;
import co.mindie.cindy.webservice.controller.manager.ControllerManager;
import co.mindie.cindy.webservice.resolver.ResolverManager;
import co.mindie.cindy.core.tools.Initializable;
import co.mindie.cindy.core.tools.Pausable;
import me.corsin.javatools.array.ArrayUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.util.List;

@Load(creationPriority = -1)
@Aspects(aspects = {Aspect.SINGLETON, Aspect.THREAD_SAFE})
@Box(needAspects = {Aspect.SINGLETON, Aspect.THREAD_SAFE}, rejectAspects = {})
public class CindyWebApp implements Pausable, Closeable, Initializable {

	// //////////////////////
	// VARIABLES
	// //////////////

	private boolean paused;

	@Wired private Configuration configuration;
	@Wired private ResolverManager resolver;
	@Wired private ControllerManager controllerManager;
	@Wired(required = false) private Appender rootLogAppender;
	@Wired private List<Pausable> pausables;

	@Core
	private ComponentBox innerBox;

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

		if (this.rootLogAppender != null) {
			Logger.getRootLogger().removeAppender(this.rootLogAppender);
		}
	}

	@MetadataModifier
	public static void injectSingletons(ComponentMetadataManagerBuilder componentMetadataManagerBuilder) {
		for (ComponentMetadata appMetadata : componentMetadataManagerBuilder.findCompatibleComponentsForClass(CindyWebApp.class)) {
			for (ComponentMetadata metadata : componentMetadataManagerBuilder.getLoadedComponentsWithAnnotation(Singleton.class)) {
				if (!ArrayUtils.arrayContains(metadata.getAspects(), Aspect.SINGLETON)) {
					metadata.setAspects(ArrayUtils.addItem(metadata.getAspects(), Aspect.SINGLETON));
				}
				if (!ArrayUtils.arrayContains(metadata.getAspects(), Aspect.THREAD_SAFE)) {
					metadata.setAspects(ArrayUtils.addItem(metadata.getAspects(), Aspect.THREAD_SAFE));
				}

				if (!appMetadata.hasDependency(metadata.getComponentClass())) {
					appMetadata.addDependency(metadata.getComponentClass(), true, false, SearchScope.LOCAL, CreationBox.CURRENT_BOX);
				}
			}
		}
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

	public ComponentBox getInnerBox() {
		return innerBox;
	}
}
