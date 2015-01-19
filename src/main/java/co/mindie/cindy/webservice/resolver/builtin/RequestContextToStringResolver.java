package co.mindie.cindy.webservice.resolver.builtin;

import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.webservice.annotation.Resolver;
import co.mindie.cindy.webservice.context.RequestContext;
import co.mindie.cindy.webservice.controller.ParamSource;
import co.mindie.cindy.webservice.resolver.ResolverContext;

@Load(creationPriority = -1)
@Resolver(managedInputClasses = RequestContext.class, managedOutputClasses = String.class)
public class RequestContextToStringResolver extends AbstractRequestContextParameterResolver<String> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	@Override
	protected String doResolve(RequestContext requestContext, String parameterName, ParamSource source, Class<?> expectedOutputType, ResolverContext resolverContext) {
		return requestContext.getStringParameter(parameterName, source);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
