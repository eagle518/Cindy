package co.mindie.cindy.resolver.builtin;

import co.mindie.cindy.automapping.Load;
import co.mindie.cindy.automapping.Resolver;
import co.mindie.cindy.context.RequestContext;
import co.mindie.cindy.exception.ResolverException;
import co.mindie.cindy.resolver.ResolverContext;
import org.apache.commons.fileupload.FileItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Load(creationPriority = -1)
@Resolver(managedInputClasses = RequestContext.class, managedOutputClasses = InputStream.class)
public class RequestContextToInputStreamResolver extends AbstractRequestContextParameterResolver<InputStream> {

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
	protected InputStream doResolve(RequestContext requestContext, String parameterName, Class<?> expectedOutputType, ResolverContext resolverContext) {
		InputStream inputStream = null;

		if (requestContext.getHttpRequest().getBodyParameters() != null) {
			List<FileItem> items = requestContext.getHttpRequest().getBodyParameters().get(parameterName);

			if (items != null && items.size() > 0) {
				FileItem item = items.get(0);
				if (!item.isFormField()) {
					try {
						inputStream = item.getInputStream();
					} catch (IOException e) {
						throw new ResolverException("Unable to get inputStream from parameter " + parameterName, e);
					}
				}
			}
		}

		return inputStream;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
