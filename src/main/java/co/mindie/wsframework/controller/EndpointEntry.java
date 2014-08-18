/////////////////////////////////////////////////
// Project : exiled-masterserver
// Package : com.kerious.exiled.masterserver.api
// MethodAssociation.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 11, 2013 at 5:26:13 PM
////////

package co.mindie.wsframework.controller;

import co.mindie.api.exceptions.BadParameterException;
import co.mindie.wsframework.automapping.Endpoint;
import co.mindie.wsframework.automapping.Param;
import co.mindie.wsframework.context.RequestContext;
import co.mindie.wsframework.controllermanager.RequestParameter;
import co.mindie.wsframework.misc.WSFrameworkException;
import co.mindie.wsframework.modelconverter.IResolverOutput;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EndpointEntry {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(EndpointEntry.class);

	final private ControllerEntry controllerEntry;
	final private Method method;
	final private Endpoint mapped;
	final private List<IParameterResolver> parameterResolvers;
	final private List<String> pathIdentifierForIndex;
	final private String path;
	final private boolean shouldResolveOutput;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public EndpointEntry(ControllerEntry controllerEntry, String path, Method method, Endpoint mapped) {
		this.controllerEntry = controllerEntry;
		this.method = method;
		this.mapped = mapped;
		this.path = path;
		this.shouldResolveOutput = method.getReturnType() == void.class ? false : mapped.resolveOutput();
		this.parameterResolvers = new ArrayList<>();
		this.pathIdentifierForIndex = new ArrayList<>();
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

	private void throwParameterError(Parameter parameter, String error) {
		throw new WSFrameworkException(Strings.format("On controller {#0}, method {#1} and parameter {#2}: {#3}", this.controllerEntry.getControllerClass(), this.method, parameter.getName(), error));
	}

	private IResolverOutput getConverter(Parameter parameter, Type genericParameterType, String name, boolean required) {
		IResolverOutput converter = null;
		Class<?> type = parameter.getType();

		if (type == List.class) {
			IResolverOutput stringToStringArray = this.controllerEntry.getApplication().getModelConverterManager().getResolverOutput(RequestParameter.class, String[].class);

			if (stringToStringArray == null) {
				this.throwParameterError(parameter, "For a using List parameter, a RequestParameter to String[] resolver must be available");
			}

			ParameterizedType aType = (ParameterizedType) genericParameterType;
			Class<?> listType = (Class<?>) aType.getActualTypeArguments()[0];
			IResolverOutput batchedConverter = null;
			try {
				Class<?> arrayType = Class.forName("[L" + listType.getName() + ";");
				batchedConverter = this.controllerEntry.getApplication().getModelConverterManager().getResolverOutput(String[].class, arrayType);
			} catch (ClassNotFoundException ignored) {
			}

			if (batchedConverter == null) {
				final IResolverOutput singleConverter = this.controllerEntry.getApplication().getModelConverterManager().getResolverOutput(String.class, listType);

				if (singleConverter == null) {
					this.throwParameterError(parameter, "No resolver exists for input String to output " + listType);
				}

				batchedConverter = (cc, inputObject, options) -> {
					if (inputObject == null) {
						return null;
					}

					String[] inputArray = (String[]) inputObject;
					Object[] outputArray = new Object[inputArray.length];

					for (int j = 0; j < inputArray.length; j++) {
						Object obj = singleConverter.createResolversAndResolve(cc, inputArray[j], options);

						if (obj == null) {
							BadParameterException ex = new BadParameterException(name);
							LOGGER.debug(Strings.format("BadParameter on {#0} {#1}\n{#2}",
									this.mapped.httpMethod().toString(),
									this.path,
									StackTraceUtils.stackTraceToString(ex)
							));
							throw ex;
						}

						outputArray[j] = obj;
					}

					return outputArray;
				};
			}

			final IResolverOutput fBatchedConverter = batchedConverter;
			converter = (cc, inputObject, options) -> {
				if (inputObject == null) {
					return null;
				}

				Object[] array = (Object[]) fBatchedConverter.createResolversAndResolve(cc,
						stringToStringArray.createResolversAndResolve(cc, inputObject, 0), options);

				if (array == null) {
					return null;
				}

				return Arrays.asList(array);
			};

		} else {
			converter = this.controllerEntry.getApplication().getModelConverterManager().getResolverOutput(RequestParameter.class, type);
		}

		if (converter == null) {
			this.throwParameterError(parameter, "No resolver exists for input String to output " + type);
		}

		return converter;
	}

	public void init() {
		Parameter[] parameters = this.method.getParameters();
		this.parameterResolvers.clear();

		Type[] genericParameterTypes = this.method.getGenericParameterTypes();
		int i = 0;
		for (Parameter parameter : parameters) {
			Type genericParameterType = genericParameterTypes[i];

			Param paramAnnotation = parameter.getAnnotation(Param.class);
			boolean required = true;
			String resolvedName = null;
			boolean needsNameResolve = this.controllerEntry.getApplication().getParameterNameResolver() != null;
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
				resolvedName = this.controllerEntry.getApplication().getParameterNameResolver().javaParameterNameToApiName(resolvedName);
			}

			if (!shouldFetchFromResource) {
				shouldFetchFromResource = this.pathIdentifierForIndex.contains(resolvedName); // Support for both with parameter resolver and without
			}

			final String name = resolvedName;
			final int fResolverOptions = resolverOptions;
			final boolean fRequired = required;
			final boolean fShouldFetchFromResource = shouldFetchFromResource;
			final IResolverOutput fConverter = this.getConverter(parameter, genericParameterType, name, required);
			IParameterResolver resolver = null;

			resolver = (e) -> {
				String stringValue = null;
				InputStream inputStreamValue = null;

				if (fShouldFetchFromResource) {
					stringValue = e.getUrlResources().get(name);
				} else {
					String[] queryParameters = e.getHttpRequest().getQueryParameters().get(name);
					if (queryParameters != null && queryParameters.length > 0) {
						stringValue = queryParameters[0];
					}

					if (stringValue == null && e.getHttpRequest().getBodyParameters() != null) {
						List<FileItem> items = e.getHttpRequest().getBodyParameters().get(name);

						if (items != null && items.size() > 0) {
							FileItem item = items.get(0);
							if (item.isFormField()) {
								stringValue = item.getString();
							} else {
								inputStreamValue = item.getInputStream();
							}
						}
					}
				}

				RequestParameter requestParameter = new RequestParameter(name);
				requestParameter.setStringValue(stringValue);
				requestParameter.setInputStream(inputStreamValue);

				Object output = fConverter.createResolversAndResolve(e.getComponentContext(), requestParameter, fResolverOptions);

				if (fRequired && output == null) {
					throw new BadParameterException(name);
				}

				return output;
			};


			this.parameterResolvers.add(resolver);
			i++;
		}
	}

	private Object[] generateParameters(RequestContext context) throws Throwable {
		Object[] parameters = new Object[this.parameterResolvers.size()];

		for (int i = 0, length = this.parameterResolvers.size(); i < length; i++) {
			parameters[i] = this.parameterResolvers.get(i).resolveParameter(context);
		}

		return parameters;
	}

	public Object invoke(Object controller, RequestContext context) throws Throwable {
		WSController theController = controller instanceof WSController ? (WSController) controller : null;

		if (theController != null) {
			theController.willStart();
		}

		try {
			try {
				final Object[] arguments = this.generateParameters(context);

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
