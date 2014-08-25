/////////////////////////////////////////////////
// Project : WSFramework
// Package :
// CindyApp.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 3, 2014 at 6:22:22 PM
////////

package co.mindie.cindy;

import java.io.Closeable;
import java.util.List;

import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.component.*;
import co.mindie.cindy.component.CindyComponent;
import co.mindie.cindy.configuration.Configuration;
import co.mindie.cindy.console.Log4jSocketConsole;
import co.mindie.cindy.context.RequestContext;
import co.mindie.cindy.controller.manager.IParameterNameResolver;
import co.mindie.cindy.controller.manager.IRequestErrorHandler;
import co.mindie.cindy.controller.builtin.SnakeCaseToCamelCaseParameterNameResolver;
import co.mindie.cindy.controller.builtin.DefaultRequestErrorHandler;
import co.mindie.cindy.database.handle.builtin.SimpleHibernateDatabaseHandle;
import co.mindie.cindy.filehandling.IFileHandler;
import co.mindie.cindy.misc.ComponentScanner;
import co.mindie.cindy.modelconverter.ResolverManager;
import co.mindie.cindy.responseserializer.JsonResponseWriter;
import co.mindie.cindy.utils.Pausable;
import me.corsin.javatools.task.MultiThreadedTaskQueue;
import me.corsin.javatools.task.TaskQueue;

import me.corsin.javatools.task.ThreadedConcurrentTaskQueue;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import co.mindie.cindy.authorizer.IRequestContextAuthorizer;
import co.mindie.cindy.controller.manager.ControllerManager;
import co.mindie.cindy.responseserializer.IResponseWriter;

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

		this.componentMetadataManager = componentMetadataManager;
		componentMetadataManager.setApplication(this);

		this.modelConverterManager = new ResolverManager(this);

		this.componentMetadataManager.loadComponent(Log4jSocketConsole.class, true);
		this.componentMetadataManager.loadComponent(this.getClass());
		this.componentMetadataManager.loadComponent(RequestContext.class, true);
		this.componentMetadataManager.loadComponent(DefaultRequestErrorHandler.class, true);
		this.componentMetadataManager.loadComponent(SnakeCaseToCamelCaseParameterNameResolver.class, true);
		this.componentMetadataManager.loadComponent(SimpleHibernateDatabaseHandle.class, true);

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

		try (ComponentContext ctx = new ComponentContext(this.getComponentContext())) {
			Object[] objects = new Object[objectClasses.length];
			for (int i = 0; i < objectClasses.length; i++) {
				objects[i] = this.createComponent(ctx, objectClasses[i]);
			}

			handler.handle(ctx, objects);
		}
	}

	public <T> void useTemporaryComponent(ITempComponentContextHandler<T> handler, Class<T> componentClass) {
		try (ComponentContext ctx = new ComponentContext(this.getComponentContext())) {
			T object = this.createComponent(ctx, componentClass);
			handler.handle(ctx, object);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T createComponent(ComponentContext componentContext, Class<T> componentClass) {
		if (componentContext == null) {
			componentContext = this.getComponentContext();
		}

		ComponentInitializer initializer = this.componentMetadataManager.createInitializer();
		T instance = (T)initializer.createComponent(componentContext, componentClass);
		initializer.init();

		return instance;
	}

	@SuppressWarnings("unchecked")
	public <T> T findOrCreateComponent(ComponentContext componentContext, Class<T> componentClass) {
		if (componentContext == null) {
			componentContext = this.getComponentContext();
		}

		T instance = (T) componentContext.findComponent(componentClass);

		if (instance == null) {
			instance = this.createComponent(componentContext, componentClass);
		}

		return instance;
	}

	@Override
	public void close() {
		if (!this.closed) {
			this.closed = true;
			this.getComponentContext().close();
		}
	}

	public void scanForComponents(String classPath) {
		ComponentScanner scanner = new ComponentScanner(classPath);
		scanner.addComponents(this);
	}

	@Override
	public void pause() {
		this.paused = true;
		List<Object> components = this.getComponentContext().findComponents(Pausable.class);

		if (components != null) {
			for (Object obj : components) {
				if (obj != this) {
					((Pausable)obj).pause();
				}
			}
		}
	}

	@Override
	public void resume() {
		this.paused = false;
		List<Object> components = this.getComponentContext().findComponents(Pausable.class);

		if (components != null) {
			for (Object obj : components) {
				if (obj != this) {
					((Pausable)obj).resume();
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
