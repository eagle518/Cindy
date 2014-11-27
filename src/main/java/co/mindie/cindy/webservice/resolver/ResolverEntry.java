/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver
// ModelConverterEntry.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on May 13, 2014 at 10:42:37 AM
////////

package co.mindie.cindy.webservice.resolver;

import co.mindie.cindy.core.exception.CindyException;
import me.corsin.javatools.string.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResolverEntry {

	////////////////////////
	// VARIABLES
	////////////////

	final private Class<?> inputClass;
	final private List<ResolverBuilder> outputs;
	final private Map<Class<?>, ResolverBuilder> outputsByOutputClass;
	private ResolverBuilder defaultConverterOutput;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ResolverEntry(Class<?> inputClass) {
		this.inputClass = inputClass;
		this.outputs = new ArrayList<>();
		this.outputsByOutputClass = new HashMap<>();
	}

	////////////////////////
	// METHODS
	////////////////

	public void addConverter(Class<?> converterClass, Class<?> outputClass, boolean isDefault, int priority) {
		ResolverBuilder output = this.outputsByOutputClass.get(outputClass);

		boolean shoudlAddConverter = true;

		if (output != null) {
			if (output.getConverterClass() != converterClass && output.getPriority() == priority) {
				throw new CindyException(Strings.format("Attempted to add converter {#0} to output {#1}, but the converter {#2}"
						+ " has been already added for this output", converterClass, outputClass, output.getConverterClass()));
			} else if (output.getPriority() < priority) {
				this.outputsByOutputClass.remove(outputClass);
				this.outputs.remove(output);
			} else {
				shoudlAddConverter = false;
			}
		} else {
			if (isDefault && this.defaultConverterOutput != null && this.defaultConverterOutput.getConverterClass() != converterClass) {
				if (this.defaultConverterOutput.getPriority() == priority) {
					throw new CindyException(Strings.format("Attempted to set converter {#0} as a default for input {#1}, but the converter {#2}"
							+ " has been already added set as default", converterClass, this.inputClass, this.defaultConverterOutput.getConverterClass()));
				} else if (this.defaultConverterOutput.getPriority() > priority) {
					shoudlAddConverter = false;
				}
			}
		}

		if (shoudlAddConverter) {
			output = new ResolverBuilder(converterClass, this.inputClass, outputClass, priority);
			if (isDefault) {
				this.defaultConverterOutput = output;
			}
			this.outputs.add(output);
			this.outputsByOutputClass.put(outputClass, output);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public ResolverBuilder getConverterOutput(Class<?> outputClass) {
		return this.outputsByOutputClass.get(outputClass);
	}

	public ResolverBuilder getDefaultConverterOutput() {
		return this.defaultConverterOutput;
	}

	public void setDefaultConverter(ResolverBuilder defaultConverter) {
		this.defaultConverterOutput = defaultConverter;
	}

	public Class<?> getInputClass() {
		return this.inputClass;
	}

	public Class<?>[] getOutputClasses() {
		Class<?>[] outputClasses = new Class[this.outputs.size()];
		for (int i = 0; i < outputClasses.length; i++) {
			outputClasses[i] = this.outputs.get(i).getOutputClass();
		}

		return outputClasses;
	}

	public List<ResolverBuilder> getOutputs() {
		return this.outputs;
	}
}
