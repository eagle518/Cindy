/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver
// AbstractModelConverterManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Feb 13, 2014 at 1:16:25 PM
////////

package co.mindie.cindy.webservice.resolver;

import co.mindie.cindy.core.annotation.Core;
import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.core.component.metadata.ComponentMetadata;
import co.mindie.cindy.core.component.metadata.ComponentMetadataManager;
import co.mindie.cindy.core.tools.Initializable;
import co.mindie.cindy.webservice.annotation.Resolver;
import co.mindie.cindy.webservice.exception.ResolverException;
import me.corsin.javatools.reflect.ReflectionUtils;
import org.apache.log4j.Logger;

import java.util.*;

@Load(creationPriority = -1)
public class ResolverManager implements Initializable {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(ResolverManager.class);

	// Kinda hacky. Didn't find a clean solution to dynamicaly provide the input and output class
	// on generated resolver classes yet. (See HibernateDAO)
	public static final String COMPONENT_CONFIG_INPUT_CLASS = "cindy.resolver_manager.managed_input_class";
	public static final String COMPONENT_CONFIG_OUTPUT_CLASS = "cindy.resolver_manager.managed_output_class";

	private Map<Class<?>, ResolverEntry> resolverEntriesByInputClass;

	@Core
	private ComponentMetadataManager metadataManager;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ResolverManager() {
		this.resolverEntriesByInputClass = new HashMap<>();
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void init() {
		for (ComponentMetadata metadata : this.metadataManager.getLoadedComponentsWithAnnotation(Resolver.class)) {
			this.addConverter(metadata.getComponentClass());
		}
	}

	public void removeAllConverters() {
		this.resolverEntriesByInputClass.clear();
	}

	public void addConverter(Class<?> resolverClass) {
		ComponentMetadata metadata = this.metadataManager.getComponentMetadata(resolverClass);

		if (metadata == null) {
			throw new IllegalArgumentException("Component " + resolverClass + " not loaded.");
		}

		Resolver annotation = metadata.getAnnotation(Resolver.class);
		Class<?>[] managedInputClasses = annotation != null ? annotation.managedInputClasses() : new Class[0];
		Class<?>[] managedOutputClasses = annotation != null ? annotation.managedOutputClasses() : new Class[0];
		boolean defaultForInputTypes = annotation != null && annotation.isDefaultForInputTypes();

		if (managedInputClasses.length == 0) {
			Class<?> inputCls = (Class<?>)metadata.getConfig(COMPONENT_CONFIG_INPUT_CLASS, false);
			if (inputCls == null) {
				inputCls = ReflectionUtils.getGenericTypeParameter(metadata.getComponentClass(), IResolver.class, 0);
			}

			if (inputCls == null) {
				throw new ResolverException("Unable to add resolver " + resolverClass + ": unable to detect managed input class");
			}

			managedInputClasses = new Class[] { inputCls };
		}

		if (managedOutputClasses.length == 0) {
			Class<?> outputCls = (Class<?>)metadata.getConfig(COMPONENT_CONFIG_OUTPUT_CLASS, false);
			if (outputCls == null) {
				outputCls = ReflectionUtils.getGenericTypeParameter(metadata.getComponentClass(), IResolver.class, 1);
			}

			if (outputCls == null) {
				throw new ResolverException("Unable to add resolver " + resolverClass + ": unable to detect managed output class");
			}

			managedOutputClasses = new Class[] { outputCls };
		}

//		System.out.println(Strings.getObjectDescription(managedInputClasses));
//		System.out.println(Strings.getObjectDescription(managedOutputClasses));

		for (Class<?> inputClass : managedInputClasses) {
			for (Class<?> outputClass : managedOutputClasses) {
				this.addConverter(metadata.getComponentClass(), inputClass, outputClass, defaultForInputTypes, metadata.getCreationPriority());
			}
		}
	}

	public void addConverter(Class<?> modelConverterClass, Class<?> inputClass, Class<?> outputClass, boolean isDefault, int priority) {
		LOGGER.trace("Registering resolver with class=" + modelConverterClass + " for input type=" + inputClass + " and output type=" + outputClass);
		ResolverEntry entry = this.resolverEntriesByInputClass.get(inputClass);

		if (entry == null) {
			entry = new ResolverEntry(inputClass);
			this.resolverEntriesByInputClass.put(inputClass, entry);
		}

		entry.addConverter(modelConverterClass, outputClass, isDefault, priority);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public ResolverBuilder getDefaultResolverOutputForInputClass(Class<?> inputClass) {
		ResolverEntry entry = this.getResolverEntry(inputClass);

		return entry != null ? entry.getDefaultConverterOutput() : null;
	}

	public IResolverBuilder getDefaultResolverOutputForInput(Object inputObject) {
		if (inputObject == null) {
			return null;
		}

		return this.getDefaultResolverOutputForInputClass(inputObject.getClass());
	}

	private ResolverEntry getResolverEntry(Class<?> inputClass) {
		ResolverEntry entry = null;

		while (entry == null && inputClass != null) {
			entry = this.resolverEntriesByInputClass.get(inputClass);
			if (entry == null) {
				inputClass = inputClass.getSuperclass();
			}
		}

		return entry;
	}

	public IResolverBuilder getResolverOutput(Class<?> inputClass, Class<?> outputClass) {
		ResolverEntry firstEntry = this.getResolverEntry(inputClass);

		if (firstEntry == null) {
			return null;
		}

		Deque<ResolverEntry> q = new ArrayDeque<>();
		Set<ResolverEntry> v = new HashSet<>();
		Map<ResolverEntry, ResolverEntry> childToParent = new HashMap<>();

		q.add(firstEntry);
		v.add(firstEntry);
		ResolverEntry outputEntry = null;

		while (!q.isEmpty()) {
			ResolverEntry t = q.poll();

			if (t.getConverterOutput(outputClass) != null) {
				outputEntry = t;
				break;
			} else {
				Class<?>[] outputClasses = t.getOutputClasses();
				for (Class<?> tOutputClass : outputClasses) {
					ResolverEntry tEntry = this.getResolverEntry(tOutputClass);

					if (tEntry != null) {
						if (!v.contains(tEntry)) {
							childToParent.put(tEntry, t);
							v.add(tEntry);
							q.add(tEntry);
						}
					}
				}
			}
		}

		if (outputEntry == null) {
			return null;
		}

		Class<?> currentOutputClass = outputClass;
		List<IResolverBuilder> outputs = new ArrayList<>();
		while (outputEntry != null) {
			outputs.add(0, outputEntry.getConverterOutput(currentOutputClass));

			currentOutputClass = outputEntry.getInputClass();
			if (outputEntry != firstEntry) {
				outputEntry = childToParent.get(outputEntry);
			} else {
				outputEntry = null;
			}
		}

		IResolverBuilder output = null;

		if (outputs.size() == 1) {
			output = outputs.get(0);
		} else {
			output = new ChainedResolverBuilder(outputs);
		}

		return output;
	}

	public Class<?> getDefaultOutputTypeForInputObject(Object object) {
		if (object == null) {
			return null;
		}

		ResolverEntry entry = this.resolverEntriesByInputClass.get(object.getClass());

		return entry != null ? entry.getDefaultConverterOutput().getOutputClass() : null;
	}

	public Class<?>[] getManagedOutputTypesForInputObject(Object object) {
		if (object == null) {
			return null;
		}

		ResolverEntry entry = this.resolverEntriesByInputClass.get(object.getClass());

		return entry != null ? entry.getOutputClasses() : null;
	}
}
