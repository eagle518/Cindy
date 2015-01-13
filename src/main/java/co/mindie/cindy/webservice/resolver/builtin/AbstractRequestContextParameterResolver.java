package co.mindie.cindy.webservice.resolver.builtin;

import co.mindie.cindy.webservice.context.RequestContext;
import co.mindie.cindy.webservice.controller.ParamSource;
import co.mindie.cindy.webservice.resolver.IResolver;
import co.mindie.cindy.webservice.resolver.MissingResolverOptionException;
import co.mindie.cindy.webservice.resolver.ResolverContext;

public abstract class AbstractRequestContextParameterResolver<T> implements IResolver<RequestContext, T> {

	////////////////////////
	// VARIABLES
	////////////////

	public static String OPTION_PARAMETER_NAME = "request_context_parameter_name";
	public static String OPTION_SOURCE = "request_context_source";

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	protected abstract T doResolve(RequestContext requestContext, String parameterName, ParamSource source, Class<?> expectedOutputType, ResolverContext resolverContext);

	@Override
	public T resolve(RequestContext requestContext, Class<?> expectedOutputType, ResolverContext resolverContext) {
		String parameterName = resolverContext.getOptions().get(OPTION_PARAMETER_NAME);

		if (parameterName == null) {
			throw new MissingResolverOptionException("PARAMETER_NAME");
		}

		ParamSource source = ParamSource.AUTO;
		String sourceStr = resolverContext.getOptions().get(OPTION_SOURCE);
		if (sourceStr != null) {
			source = ParamSource.valueOf(sourceStr);
		}

		return this.doResolve(requestContext, parameterName, source, expectedOutputType, resolverContext);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
