/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver
// ModelConverterEntry.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on May 13, 2014 at 10:42:37 AM
////////

package co.mindie.cindy.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.mindie.cindy.CindyWebApp;
import co.mindie.cindy.component.ComponentMetadataManager;
import co.mindie.cindy.exception.CindyException;
import me.corsin.javatools.string.Strings;

public class ResolverEntry {

	////////////////////////
	// VARIABLES
	////////////////

	final private ComponentMetadataManager metadataManager;
	final private Class<?> inputClass;
	final private List<ResolverOutput> outputs;
	final private Map<Class<?>, ResolverOutput> outputsByOutputClass;
	private ResolverOutput defaultConverterOutput;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ResolverEntry(ComponentMetadataManager metadataManager, Class<?> inputClass) {
		this.metadataManager = metadataManager;
		this.inputClass = inputClass;
		this.outputs = new ArrayList<>();
		this.outputsByOutputClass = new HashMap<>();
	}

	////////////////////////
	// METHODS
	////////////////

	public void addConverter(Class<?> converterClass, Class<?> outputClass, boolean isDefault) {
		ResolverOutput output = this.outputsByOutputClass.get(outputClass);

		if (output != null) {
			if (output.getConverterClass() != converterClass) {
				throw new CindyException(Strings.format("Attempted to add converter {#0} to output {#1}, but the converter {#2}"
						+ " has been already added for this output", converterClass, outputClass, output.getConverterClass()));
			}
		} else {
			if (isDefault && this.defaultConverterOutput != null && this.defaultConverterOutput.getConverterClass() != converterClass) {
				throw new CindyException(Strings.format("Attempted to set converter {#0} as a default for input {#1}, but the converter {#2}"
							+ " has been already added set as default", converterClass, this.inputClass, this.defaultConverterOutput.getConverterClass()));
			}

			output = new ResolverOutput(this.metadataManager, converterClass, this.inputClass, outputClass);
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

	public ResolverOutput getConverterOutput(Class<?> outputClass) {
		return this.outputsByOutputClass.get(outputClass);
	}

	public ResolverOutput getDefaultConverterOutput() {
		return this.defaultConverterOutput;
	}

	public void setDefaultConverter(ResolverOutput defaultConverter) {
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

	public List<ResolverOutput> getOutputs() {
		return this.outputs;
	}
}
