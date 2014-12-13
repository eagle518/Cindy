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
import co.mindie.cindy.automapping.Box;
import co.mindie.cindy.automapping.Component;
import co.mindie.cindy.automapping.CreationBox;
import co.mindie.cindy.automapping.Load;
import co.mindie.cindy.automapping.SearchScope;
import co.mindie.cindy.automapping.Singleton;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.automapping.WiredCore;
import co.mindie.cindy.component.*;
import co.mindie.cindy.component.box.ComponentBox;
import co.mindie.cindy.configuration.Configuration;
import co.mindie.cindy.controller.manager.ControllerManager;
import co.mindie.cindy.resolver.ResolverManager;
import co.mindie.cindy.utils.Initializable;
import co.mindie.cindy.utils.Pausable;
import me.corsin.javatools.array.ArrayUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.util.List;

@Load(creationPriority = -1)
@Component(aspects = {ComponentAspect.SINGLETON, ComponentAspect.THREAD_SAFE})
@Box(needAspects = {ComponentAspect.SINGLETON, ComponentAspect.THREAD_SAFE}, rejectAspects = {})
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

	@WiredCore private ComponentBox innerBox;

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
				if (!ArrayUtils.arrayContains(metadata.getAspects(), ComponentAspect.SINGLETON)) {
					metadata.setAspects(ArrayUtils.addItem(metadata.getAspects(), ComponentAspect.SINGLETON));
				}
				if (!ArrayUtils.arrayContains(metadata.getAspects(), ComponentAspect.THREAD_SAFE)) {
					metadata.setAspects(ArrayUtils.addItem(metadata.getAspects(), ComponentAspect.THREAD_SAFE));
				}

				if (!appMetadata.hasDependency(metadata.getComponentClass())) {
					appMetadata.addDependency(metadata.getComponentClass(), true, false, SearchScope.GLOBAL, CreationBox.CURRENT_BOX);
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
