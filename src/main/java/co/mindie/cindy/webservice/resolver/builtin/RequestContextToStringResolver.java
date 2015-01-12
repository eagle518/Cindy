package co.mindie.cindy.webservice.resolver.builtin;

import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.webservice.annotation.Resolver;
import co.mindie.cindy.webservice.context.RequestContext;
import co.mindie.cindy.webservice.controller.ParamSource;
import co.mindie.cindy.webservice.exception.ResolverException;
import co.mindie.cindy.webservice.resolver.ResolverContext;
import org.apache.commons.fileupload.FileItem;

import java.io.UnsupportedEncodingException;
import java.util.List;

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
		String stringValue = null;

		if (source == ParamSource.AUTO || source == ParamSource.URL) {
			stringValue = requestContext.getUrlResources().get(parameterName);
		}

		if (source == ParamSource.QUERY || (stringValue == null && source == ParamSource.AUTO)) {
			String[] queryParameters = requestContext.getHttpRequest().getQueryParameters().get(parameterName);
			if (queryParameters != null && queryParameters.length > 0) {
				stringValue = queryParameters[0];
			}
		}

		if (source == ParamSource.BODY || (stringValue == null && source == ParamSource.AUTO)) {
			if (requestContext.getHttpRequest().getBodyParameters() != null) {
				List<FileItem> items = requestContext.getHttpRequest().getBodyParameters().get(parameterName);

				if (items != null && items.size() > 0) {
					FileItem item = items.get(0);
					if (item.isFormField()) {
						try {
							stringValue = item.getString("UTF-8");
						} catch (UnsupportedEncodingException e) {
							throw new ResolverException("Unable to get parameterName " + parameterName, e);
						}
					}
				}
			}
		}

		return stringValue;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
