/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.modelconverter
// ModelConverterEntry.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on May 13, 2014 at 10:42:37 AM
////////

package co.mindie.wsframework.modelconverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.corsin.javatools.string.Strings;
import co.mindie.wsframework.WSApplication;
import co.mindie.wsframework.exception.WSFrameworkException;

public class ResolverEntry {

	////////////////////////
	// VARIABLES
	////////////////

	final private Class<?> inputClass;
	private List<ModelConverterOutput> outputs;
	private Map<Class<?>, ModelConverterOutput> outputsByOutputClass;
	private ModelConverterOutput defaultConverterOutput;
	final private WSApplication application;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ResolverEntry(WSApplication application, Class<?> inputClass) {
		this.application = application;
		this.inputClass = inputClass;
		this.outputs = new ArrayList<>();
		this.outputsByOutputClass = new HashMap<>();
	}

	////////////////////////
	// METHODS
	////////////////

	public void addConverter(Class<?> converterClass, Class<?> outputClass, boolean isDefault) {
		ModelConverterOutput output = this.outputsByOutputClass.get(outputClass);

		if (output != null) {
			if (output.getConverterClass() != converterClass) {
				throw new WSFrameworkException(Strings.format("Attempted to add converter {#0} to output {#1}, but the converter {#2}"
						+ " has been already added for this output", converterClass, outputClass, output.getConverterClass()));
			}
		} else {
			if (isDefault && this.defaultConverterOutput != null && this.defaultConverterOutput.getConverterClass() != converterClass) {
				throw new WSFrameworkException(Strings.format("Attempted to set converter {#0} as a default for input {#1}, but the converter {#2}"
							+ " has been already added set as default", converterClass, this.inputClass, this.defaultConverterOutput.getConverterClass()));
			}

			output = new ModelConverterOutput(this.application, converterClass, this.inputClass, outputClass);
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

	public ModelConverterOutput getConverterOutput(Class<?> outputClass) {
		return this.outputsByOutputClass.get(outputClass);
	}

	public ModelConverterOutput getDefaultConverterOutput() {
		return this.defaultConverterOutput;
	}

	public void setDefaultConverter(ModelConverterOutput defaultConverter) {
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

	public List<ModelConverterOutput> getOutputs() {
		return this.outputs;
	}
}
