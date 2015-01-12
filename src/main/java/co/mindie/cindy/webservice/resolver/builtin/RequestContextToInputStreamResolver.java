package co.mindie.cindy.webservice.resolver.builtin;

import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.webservice.annotation.Resolver;
import co.mindie.cindy.webservice.context.RequestContext;
import co.mindie.cindy.webservice.controller.ParamSource;
import co.mindie.cindy.webservice.exception.ResolverException;
import co.mindie.cindy.webservice.resolver.ResolverContext;
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
	protected InputStream doResolve(RequestContext requestContext, String parameterName, ParamSource source, Class<?> expectedOutputType, ResolverContext resolverContext) {
		InputStream inputStream = null;

		if (source == ParamSource.AUTO || source == ParamSource.BODY) {
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
		}

		return inputStream;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
