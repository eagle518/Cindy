package co.mindie.cindy.webservice.resolver.builtin;

import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.core.tools.io.InputStreamCreator;
import co.mindie.cindy.webservice.annotation.Resolver;
import co.mindie.cindy.webservice.context.RequestContext;
import co.mindie.cindy.webservice.controller.ParamSource;
import co.mindie.cindy.webservice.resolver.ResolverContext;
import org.apache.commons.fileupload.FileItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Load(creationPriority = -1)
@Resolver(managedInputClasses = RequestContext.class, managedOutputClasses = InputStreamCreator.class)
public class RequestContextToInputStreamCreatorResolver extends AbstractRequestContextParameterResolver<InputStreamCreator> {

	////////////////////////
	// VARIABLES
	////////////////

	public static class FileItemInputStreamCreator implements InputStreamCreator {

		final private FileItem fileItem;

		public FileItemInputStreamCreator(FileItem fileItem) {
			this.fileItem = fileItem;
		}

		@Override
		public InputStream createInputStream() throws IOException {
			return this.fileItem.getInputStream();
		}

		@Override
		public long getInputStreamLength() {
			return this.fileItem.getSize();
		}
	}

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	@Override
	protected InputStreamCreator doResolve(RequestContext requestContext, String parameterName, ParamSource source, Class<?> expectedOutputType, ResolverContext resolverContext) {
		if (source == ParamSource.AUTO || source == ParamSource.BODY) {
			if (requestContext.getHttpRequest().getBodyParameters() != null) {
				List<FileItem> items = requestContext.getHttpRequest().getBodyParameters().get(parameterName);

				if (items != null && items.size() > 0) {
					FileItem item = items.get(0);
					if (!item.isFormField()) {
						return new FileItemInputStreamCreator(item);
					}
				}
			}
		}

		return null;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
