package co.mindie.cindy.webservice.resolver.builtin;

import co.mindie.cindy.webservice.context.RequestContext;
import co.mindie.cindy.webservice.resolver.IResolver;
import co.mindie.cindy.webservice.resolver.MissingResolverOptionException;
import co.mindie.cindy.webservice.resolver.ResolverContext;

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
