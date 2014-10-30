/////////////////////////////////////////////////
// Project : exiled-masterserver
// Package : com.kerious.exiled.masterserver.api
// MethodAssociation.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 11, 2013 at 5:26:13 PM
////////

package co.mindie.cindy.controller.manager.entry;

import co.mindie.cindy.automapping.Endpoint;
import co.mindie.cindy.automapping.Param;
import co.mindie.cindy.component.ComponentInitializer;
import co.mindie.cindy.component.box.ComponentBox;
import co.mindie.cindy.component.ComponentMetadataManager;
import co.mindie.cindy.context.RequestContext;
import co.mindie.cindy.controller.CindyController;
import co.mindie.cindy.controller.manager.IParameterNameResolver;
import co.mindie.cindy.controller.manager.RequestParameter;
import co.mindie.cindy.exception.BadParameterException;
import co.mindie.cindy.exception.CindyException;
import co.mindie.cindy.resolver.*;
import co.mindie.cindy.resolver.builtin.ArrayToArrayResolver;
import co.mindie.cindy.resolver.builtin.ArrayToListResolver;
import me.corsin.javatools.exception.StackTraceUtils;
import me.corsin.javatools.string.Strings;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class EndpointEntry {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(EndpointEntry.class);

	final private ControllerEntry controllerEntry;
	final private Method method;
	final private Endpoint mapped;
	final private List<RequestParameterCreator> parameterResolvers;
	final private List<IResolverBuilder> parameterResolverBuilders;
	final private List<String> pathIdentifierForIndex;
	final private String path;
	final private boolean shouldResolveOutput;
	final private Deque<RequestHandler> pool;
	final private Class<?> requestHandlerType;
	private IResolverBuilder outputResolverBuilder;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public EndpointEntry(ControllerEntry controllerEntry, String path, Method method, Endpoint mapped, Class<?> requestHandlerType) {
		this.controllerEntry = controllerEntry;
		this.method = method;
		this.mapped = mapped;
		this.path = path;
		this.pool = new ArrayDeque<>();
		this.shouldResolveOutput = method.getReturnType() != void.class && mapped.resolveOutput();
		this.parameterResolverBuilders = new ArrayList<>();
		this.parameterResolvers = new ArrayList<>();
		this.pathIdentifierForIndex = new ArrayList<>();
		this.requestHandlerType = requestHandlerType;
	}

	////////////////////////
	// METHODS
	////////////////

	public void addPathIdentifier(String identifier, int index) {
		while (this.pathIdentifierForIndex.size() <= index) {
			this.pathIdentifierForIndex.add(null);
		}

		this.pathIdentifierForIndex.set(index, identifier);
	}

	public void preload(ComponentMetadataManager metadataManager, ComponentBox box) {
		this.createRequestHandler(metadataManager, box, true).release(true);
	}

	private IResolver createResolver(IResolverBuilder resolverBuilder, ComponentInitializer initializer, ComponentBox box) {
		return resolverBuilder.findOrCreateResolver(initializer, box);
	}

	public RequestHandler<?> createRequestHandler(ComponentMetadataManager metadataManager, ComponentBox box, boolean useReusePool) {
		RequestHandler<?> requestHandler = null;

		if (useReusePool) {
			synchronized (this.pool) {
				if (!this.pool.isEmpty()) {
					requestHandler = this.pool.poll();
				}
			}
		}

		if (requestHandler == null) {
			ComponentInitializer initializer = metadataManager.createInitializer();

			requestHandler = (RequestHandler<?>)initializer.createComponent(this.requestHandlerType, box).getInstance();
			requestHandler.setEndpointEntry(this);

			List<IResolver> parameterResolvers = new ArrayList<>();
			ComponentBox resolverBox = box.createChildBox(true);

			for (IResolverBuilder resolverBuilder : this.parameterResolverBuilders) {
				parameterResolvers.add(this.createResolver(resolverBuilder, initializer, resolverBox));
			}
			requestHandler.setParametersResolver(parameterResolvers);

			if (this.outputResolverBuilder != null) {
				requestHandler.setOutputResolver(this.createResolver(this.outputResolverBuilder, initializer, resolverBox));
			}

			initializer.init();
		}

		return requestHandler;
	}

	public void releaseRequestHandler(RequestHandler requestHandler, boolean useReusePool) {
		requestHandler.reset();

		if (useReusePool) {
			synchronized (this.pool) {
				this.pool.add(requestHandler);
			}
		}
	}

	private void throwMethodError(String error) {
		throw new CindyException(Strings.format("On controller {#0} and method {#1}: {#2}", this.controllerEntry.getControllerClass(), this.method, error));
	}

	private void throwParameterError(Parameter parameter, String error) {
		throw new CindyException(Strings.format("On controller {#0}, method {#1} and parameter {#2}: {#3}", this.controllerEntry.getControllerClass(), this.method, parameter.getName(), error));
	}

	private IResolverBuilder getResolverBuilder(ResolverManager resolverManager, Parameter parameter, Type genericParameterType, String name, boolean required) {
		IResolverBuilder converter = null;
		Class<?> type = parameter.getType();

		if (type == List.class) {
			// RequestParameter -> String[]
			IResolverBuilder requestParameterToStringArray = resolverManager.getResolverOutput(RequestParameter.class, String[].class);

			if (requestParameterToStringArray == null) {
				this.throwParameterError(parameter, "For a using List parameter, a RequestParameter to String[] resolver must be available");
			}

			ParameterizedType aType = (ParameterizedType) genericParameterType;
			Class<?> listType = (Class<?>) aType.getActualTypeArguments()[0];

			// String[] -> Object[]
			IResolverBuilder stringArrayToObjectArrayResolver = null;

			try {
				Class<?> arrayType = Class.forName("[L" + listType.getName() + ";");
				stringArrayToObjectArrayResolver = resolverManager.getResolverOutput(String[].class, arrayType);
			} catch (ClassNotFoundException ignored) {
			}

			if (stringArrayToObjectArrayResolver == null) {
				// Generating one String[] -> Object[]
				final IResolverBuilder singleConverter = resolverManager.getResolverOutput(String.class, listType);

				if (singleConverter == null) {
					this.throwParameterError(parameter, "No resolver exists for input String to output " + listType);
				}

				stringArrayToObjectArrayResolver = (initializer, box) -> {
					final IResolver resolver = singleConverter.findOrCreateResolver(initializer, box);

					return new ArrayToArrayResolver<>(listType, resolver);
				};
			}

			// Object[] -> List
			IResolverBuilder arrayToListResolverBuilder = (initializer, box) -> new ArrayToListResolver();

			// RequestParameter -> String[] -> Object[] -> List
			converter = new ChainedResolverBuilder(Arrays.asList(requestParameterToStringArray, stringArrayToObjectArrayResolver, arrayToListResolverBuilder));
		} else {
			converter = resolverManager.getResolverOutput(RequestParameter.class, type);
		}

		if (converter == null) {
			this.throwParameterError(parameter, "No resolver exists for input RequestParameter to output " + type);
		}

		return converter;
	}

	private void appendSubResolverBuilder(ResolverManager resolverManager, Class<?> componentType, List<IResolverBuilder> subResolverBuilders) {
		final IResolverBuilder subResolverBuilder = resolverManager.getDefaultResolverOutputForInputClass(componentType);

		if (subResolverBuilder == null) {
			this.throwMethodError("Return type is dynamic and no default resolver found for type " + componentType);
		}

		subResolverBuilders.add(subResolverBuilder);
	}
	public void init(IParameterNameResolver parameterNameResolver, ResolverManager resolverManager) {
		Parameter[] parameters = this.method.getParameters();
		this.parameterResolvers.clear();
		this.parameterResolverBuilders.clear();

		if (this.shouldResolveOutput) {
			ResolverBuilder defaultBuilder = resolverManager.getDefaultResolverOutputForInputClass(this.method.getReturnType());

			if (defaultBuilder != null && defaultBuilder.isDynamic()) {
				final List<IResolverBuilder> subResolverBuilders = new ArrayList<>();

				if (this.method.getReturnType().getComponentType() != null) {
					this.appendSubResolverBuilder(resolverManager, this.method.getReturnType().getComponentType(), subResolverBuilders);
				} else if (this.method.getGenericReturnType() instanceof ParameterizedType) {
					ParameterizedType aType = (ParameterizedType)this.method.getGenericReturnType();

					for (Type type : aType.getActualTypeArguments()) {
						this.appendSubResolverBuilder(resolverManager, (Class<?>)type, subResolverBuilders);
					}
				} else {
					this.throwMethodError("Return type is dynamic but the returned type is not generic nor an array");
				}

				this.outputResolverBuilder = (initializer, box) -> {
					IDynamicResolver baseResolver = (IDynamicResolver)defaultBuilder.findOrCreateResolver(initializer, box);

					for (IResolverBuilder subResolverBuilder : subResolverBuilders) {
						baseResolver.appendSubResolver(subResolverBuilder.findOrCreateResolver(initializer, box));
					}

					return baseResolver;
				};


			} else {
				this.outputResolverBuilder = defaultBuilder;
			}
		}

		Type[] genericParameterTypes = this.method.getGenericParameterTypes();
		int i = 0;
		for (Parameter parameter : parameters) {
			Type genericParameterType = genericParameterTypes[i];

			Param paramAnnotation = parameter.getAnnotation(Param.class);
			boolean required = true;
			String resolvedName = null;
			boolean needsNameResolve = parameterNameResolver != null;
			int resolverOptions = 0;

			if (paramAnnotation != null) {
				required = paramAnnotation.required();
				resolverOptions = paramAnnotation.resolverOptions();
				if (!Strings.isNullOrEmpty(paramAnnotation.name())) {
					resolvedName = paramAnnotation.name();
					needsNameResolve = false;
				}
			}

			if (resolvedName == null) {
				resolvedName = parameter.getName();
			}

			boolean shouldFetchFromResource = this.pathIdentifierForIndex.contains(resolvedName);

			if (needsNameResolve && !shouldFetchFromResource) {
				resolvedName = parameterNameResolver.javaParameterNameToApiName(resolvedName);
			}

			if (!shouldFetchFromResource) {
				shouldFetchFromResource = this.pathIdentifierForIndex.contains(resolvedName); // Support for both with parameter resolver and without
			}

			final String name = resolvedName;
			final int fResolverOptions = resolverOptions;
			final boolean fRequired = required;
			final boolean fShouldFetchFromResource = shouldFetchFromResource;
			final IResolverBuilder fConverter = this.getResolverBuilder(resolverManager, parameter, genericParameterType, name, required);

			this.parameterResolverBuilders.add(fConverter);

			RequestParameterCreator requestParameterCreator;

			requestParameterCreator = (e) -> {
				RequestContext requestContext = e.getRequestContext();
				String stringValue = null;
				InputStream inputStreamValue = null;

				if (fShouldFetchFromResource) {
					stringValue = requestContext.getUrlResources().get(name);
				} else {
					String[] queryParameters = requestContext.getHttpRequest().getQueryParameters().get(name);
					if (queryParameters != null && queryParameters.length > 0) {
						stringValue = queryParameters[0];
					}

					if (stringValue == null && requestContext.getHttpRequest().getBodyParameters() != null) {
						List<FileItem> items = requestContext.getHttpRequest().getBodyParameters().get(name);

						if (items != null && items.size() > 0) {
							FileItem item = items.get(0);
							if (item.isFormField()) {
								stringValue = item.getString("UTF-8");
							} else {
								inputStreamValue = item.getInputStream();
							}
						}
					}
				}

				RequestParameter requestParameter = new RequestParameter(name, fResolverOptions, fRequired);
				requestParameter.setStringValue(stringValue);
				requestParameter.setInputStream(inputStreamValue);

				return requestParameter;
			};


			this.parameterResolvers.add(requestParameterCreator);
			i++;
		}
	}

	private Object[] generateParameters(RequestHandler<?> requestHandler) throws Throwable {
		Object[] parameters = new Object[this.parameterResolvers.size()];

		for (int i = 0, length = this.parameterResolvers.size(); i < length; i++) {
			RequestParameter requestParameter = this.parameterResolvers.get(i).createParameter(requestHandler);

			Object output = null;
			try {
				IResolver<RequestParameter, ?> resolver =  requestHandler.getParametersResolver().get(i);
				output = resolver.resolve(requestParameter, null, requestParameter.getResolverOptions());
			} catch (Exception ex) {
				throw new CindyException("Error while resolving the parameter: " + requestParameter.getName(), ex);
			}

			if (requestParameter.isRequired() && output == null) {
				throw new BadParameterException(requestParameter.getName(), this.mapped.httpMethod(), this.getPath());
			}

			parameters[i] = output;
		}

		return parameters;
	}

	public Object invoke(RequestHandler<?> requestHandler) throws Throwable {
		Object controller = requestHandler.getController();
		CindyController theController = controller instanceof CindyController ? (CindyController) controller : null;

		if (theController != null) {
			theController.willStart();
		}

		try {
			try {
				final Object[] arguments = this.generateParameters(requestHandler);

				return this.method.invoke(controller, arguments);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		} finally {
			if (theController != null) {
				theController.didEnd();
			}
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final ControllerEntry getControllerEntry() {
		return this.controllerEntry;
	}

	public final Method getMethod() {
		return this.method;
	}

	public final String[] getRequiredAuthorizations() {
		return this.mapped.requiredPermissions();
	}

	public final Class<?> getResponseWriterClass() {
		return this.mapped.responseWriterClass();
	}

	public String getPath() {
		return this.path;
	}

	public String getPathIdentifierForIndex(int index) {
		if (index < this.pathIdentifierForIndex.size()) {
			return this.pathIdentifierForIndex.get(index);
		}

		return null;
	}

	public List<String> getPathIdentifierForIndexes() {
		return this.pathIdentifierForIndex;
	}

	public boolean shouldResolveOutput() {
		return this.shouldResolveOutput;
	}

	public int getOutputResolverOptions() {
		return this.mapped.outputResolverOptions();
	}
}
