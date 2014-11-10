package co.mindie.cindy.resolver.builtin;

import co.mindie.cindy.automapping.Load;
import co.mindie.cindy.automapping.Resolver;
import co.mindie.cindy.context.RequestContext;
import co.mindie.cindy.exception.ResolverException;
import co.mindie.cindy.resolver.ResolverContext;
import org.apache.commons.fileupload.FileItem;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Load(creationPriority = -1)
@Resolver(managedInputClasses = RequestContext.class, managedOutputClasses = String.class)
public class RequestContextToStringResolver extends AbstractRequestContextParameterResolver<String> {

	////////////////////////
	// VARIABLES
	////////////////

	public static String OPTION_FETCH_FROM_RESOURCE = "request_context_fetch_from_resource";

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	@Override
	protected String doResolve(RequestContext requestContext, String parameterName, Class<?> expectedOutputType, ResolverContext resolverContext) {
		String stringValue = null;

		if (resolverContext.getOptions().getBoolean(OPTION_FETCH_FROM_RESOURCE, false)) {
			stringValue = requestContext.getUrlResources().get(parameterName);
		} else {
			String[] queryParameters = requestContext.getHttpRequest().getQueryParameters().get(parameterName);
			if (queryParameters != null && queryParameters.length > 0) {
				stringValue = queryParameters[0];
			}

			if (stringValue == null && requestContext.getHttpRequest().getBodyParameters() != null) {
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
