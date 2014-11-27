package co.mindie.cindy.webservice.resolver.builtin;

import co.mindie.cindy.core.exception.CindyException;
import co.mindie.cindy.webservice.resolver.IDynamicResolver;
import co.mindie.cindy.webservice.resolver.IResolver;
import co.mindie.cindy.webservice.resolver.ResolverContext;

import java.lang.reflect.Array;

public class ArrayToArrayResolver<Input, Output> implements IDynamicResolver<Input[], Output[]> {

	////////////////////////
	// VARIABLES
	////////////////

	private IResolver<Input, Output> singleResolver;
	final private Class<Output> outputClass;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ArrayToArrayResolver() {
		this((Class)Object.class, null);
	}

	public ArrayToArrayResolver(Class<Output> outputClass, IResolver<Input, Output> singleResolver) {
		this.singleResolver = singleResolver;
		this.outputClass = outputClass;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public Output[] resolve(Input[] inputArray, Class<?> expectedOutputType, ResolverContext resolverContext) {
		if (this.singleResolver == null) {
			throw new CindyException("ArrayToArrayResolver needs to has a sub resolver");
		}

		if (inputArray == null) {
			return null;
		}

		Output[] outputArray = (Output[])Array.newInstance(this.outputClass, inputArray.length);

		for (int j = 0; j < inputArray.length; j++) {
			Output obj = this.singleResolver.resolve(inputArray[j], expectedOutputType, resolverContext);

			outputArray[j] = obj;
		}

		return outputArray;
	}

	@Override
	public void appendSubResolver(IResolver resolver) {
		this.singleResolver = resolver;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}