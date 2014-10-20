/////////////////////////////////////////////////
// Project : WSFramework
// Package :
// CindyApp.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 3, 2014 at 6:22:22 PM
////////

package co.mindie.cindy;

import co.mindie.cindy.authorizer.IRequestContextAuthorizer;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.component.*;
import co.mindie.cindy.component.ComponentBox;
import co.mindie.cindy.configuration.Configuration;
import co.mindie.cindy.controller.manager.ControllerManager;
import co.mindie.cindy.controller.manager.IParameterNameResolver;
import co.mindie.cindy.controller.manager.IRequestErrorHandler;
import co.mindie.cindy.filehandling.IFileHandler;
import co.mindie.cindy.misc.ComponentScanner;
import co.mindie.cindy.resolver.ResolverManager;
import co.mindie.cindy.responseserializer.IResponseWriter;
import co.mindie.cindy.responseserializer.JsonResponseWriter;
import co.mindie.cindy.utils.Pausable;
import me.corsin.javatools.task.TaskQueue;
import me.corsin.javatools.task.ThreadedConcurrentTaskQueue;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;

import java.io.Closeable;
import java.util.List;

public class CindyApp extends CindyComponent implements Closeable, Pausable {

	// //////////////////////
	// VARIABLES
	// //////////////

	final private ComponentMetadataManager componentMetadataManager;
	final private TaskQueue taskQueue;
	final private ResolverManager modelConverterManager;
	final private ControllerManager controllerManager;
	private boolean closed;
	private boolean paused;

	@Wired
	private Configuration configuration;
	@Wired(required = false) private IFileHandler fileHandler;
	@Wired(required = false) private IParameterNameResolver parameterNameResolver;
	@Wired(required = false) private IRequestContextAuthorizer requestContextAuthorizer;
	@Wired(required = false) private IRequestErrorHandler requestErrorHandler;
	@Wired(required = false) private IResponseWriter defaultResponseWriter;
	@Wired(required = false) private Appender rootLogAppender;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public CindyApp(ComponentMetadataManager componentMetadataManager) {
		super();
		DateTimeZone.setDefault(DateTimeZone.UTC);

		this.componentMetadataManager = componentMetadataManager;
		componentMetadataManager.setApplication(this);

		this.modelConverterManager = new ResolverManager(this);

		this.componentMetadataManager.loadComponent(this.getClass());

		this.controllerManager = new ControllerManager();
		this.controllerManager.setApplication(this);

		this.taskQueue = new ThreadedConcurrentTaskQueue();
	}

	// //////////////////////
	// METHODS
	// //////////////

	@Override
	public void init() {
		if (!this.isInitialized()) {
			super.init();

			if (this.rootLogAppender != null) {
				Logger.getRootLogger().addAppender(this.rootLogAppender);
			}
			this.controllerManager.init();
		}
	}

	public void addController(Class<?> controllerClass, String basePath) {
		this.componentMetadataManager.loadComponent(controllerClass);
		this.controllerManager.addController(controllerClass, basePath);
	}

	public <T> T createComponent(Class<T> componentClass) {
		return this.createComponent(null, componentClass);
	}

	public <T, T2> void useTemporaryComponents(ITempComponentsContextHandler handler, Class<?>... objectClasses) {

		try (ComponentBox ctx = new ComponentBox(this.getComponentBox())) {
			Object[] objects = new Object[objectClasses.length];
			for (int i = 0; i < objectClasses.length; i++) {
				objects[i] = this.createComponent(ctx, objectClasses[i]);
			}

			handler.handle(ctx, objects);
		}
	}

	public <T> void useTemporaryComponent(ITempComponentContextHandler<T> handler, Class<T> componentClass) {
		try (ComponentBox ctx = new ComponentBox(this.getComponentBox())) {
			T object = this.createComponent(ctx, componentClass);
			handler.handle(ctx, object);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T createComponent(ComponentBox componentBox, Class<T> componentClass) {
		if (componentBox == null) {
			componentBox = this.getComponentBox();
		}

		ComponentInitializer initializer = this.componentMetadataManager.createInitializer();
		T instance = (T) initializer.createComponent(componentBox, componentClass);
		initializer.init();

		return instance;
	}

	@SuppressWarnings("unchecked")
	public <T> T findOrCreateComponent(ComponentBox componentBox, Class<T> componentClass) {
		if (componentBox == null) {
			componentBox = this.getComponentBox();
		}

		T instance = (T) componentBox.findComponent(componentClass);

		if (instance == null) {
			instance = this.createComponent(componentBox, componentClass);
		}

		return instance;
	}

	@Override
	public void close() {
		if (!this.closed) {
			this.closed = true;
			this.getComponentBox().close();
		}
	}

	public void scanForComponents(String classPath) {
		ComponentScanner scanner = new ComponentScanner(classPath);
		scanner.addComponents(this);
	}

	@Override
	public void pause() {
		this.paused = true;
		List<Object> components = this.getComponentBox().findComponents(Pausable.class);

		if (components != null) {
			for (Object obj : components) {
				if (obj != this) {
					((Pausable) obj).pause();
				}
			}
		}
	}

	@Override
	public void resume() {
		this.paused = false;
		List<Object> components = this.getComponentBox().findComponents(Pausable.class);

		if (components != null) {
			for (Object obj : components) {
				if (obj != this) {
					((Pausable) obj).resume();
				}
			}
		}
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	public TaskQueue getTaskQueue() {
		return this.taskQueue;
	}

	public Configuration getConfiguration() {
		return this.configuration;
	}

	public IRequestErrorHandler getRequestErrorHandler() {
		return this.requestErrorHandler;
	}

	public IRequestContextAuthorizer getRequestContextAuthorizer() {
		return this.requestContextAuthorizer;
	}

	public IFileHandler getFileHandler() {
		return this.fileHandler;
	}

	public void setFileHandler(IFileHandler fileHandler) {
		this.fileHandler = fileHandler;
	}

	public ControllerManager getControllerManager() {
		return this.controllerManager;
	}

	public IParameterNameResolver getParameterNameResolver() {
		return this.parameterNameResolver;
	}

	public ComponentMetadataManager getComponentMetadataManager() {
		return this.componentMetadataManager;
	}

	public boolean isClosed() {
		return this.closed;
	}

	public ResolverManager getModelConverterManager() {
		return this.modelConverterManager;
	}

	public IResponseWriter getDefaultResponseWriter() {
		if (this.defaultResponseWriter == null) {
			return new JsonResponseWriter();
		}
		return this.defaultResponseWriter;
	}

	public void setDefaultResponseWriter(IResponseWriter defaultResponseWriter) {
		this.defaultResponseWriter = defaultResponseWriter;
	}

	public void setMaintenanceModeEnabled(boolean value) {
		this.controllerManager.setMaintenanceModeEnabled(value);
	}

	public boolean isMaintenanceModeEnabled() {
		return this.controllerManager.isMaintenanceModeEnabled();
	}

	@Override
	public boolean isPaused() {
		return this.paused;
	}
}
