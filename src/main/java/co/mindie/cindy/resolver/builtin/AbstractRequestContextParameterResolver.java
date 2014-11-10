package co.mindie.cindy.resolver.builtin;

import co.mindie.cindy.context.RequestContext;
import co.mindie.cindy.resolver.IResolver;
import co.mindie.cindy.resolver.MissingResolverOptionException;
import co.mindie.cindy.resolver.ResolverContext;

public abstract class AbstractRequestContextParameterResolver<T> implements IResolver<RequestContext, T> {

	////////////////////////
	// VARIABLES
	////////////////

	public static String OPTION_PARAMETER_NAME = "request_context_parameter_name";

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	protected abstract T doResolve(RequestContext requestContext, String parameterName, Class<?> expectedOutputType, ResolverContext resolverContext);

	@Override
	public T resolve(RequestContext requestContext, Class<?> expectedOutputType, ResolverContext resolverContext) {
		String parameterName = resolverContext.getOptions().get(OPTION_PARAMETER_NAME);

		if (parameterName == null) {
			throw new MissingResolverOptionException("PARAMETER_NAME");
		}

		return this.doResolve(requestContext, parameterName, expectedOutputType, resolverContext);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
