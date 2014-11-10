package co.mindie.cindy.resolver.builtin;

import co.mindie.cindy.automapping.Load;
import co.mindie.cindy.automapping.Resolver;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.context.RequestContext;
import co.mindie.cindy.controller.manager.RequestParameter;
import co.mindie.cindy.dao.domain.PageRequest;
import co.mindie.cindy.resolver.ResolverContext;
import co.mindie.cindy.resolver.ResolverOptions;

@Load(creationPriority = -1)
@Resolver(managedInputClasses = RequestContext.class, managedOutputClasses = PageRequest.class)
public class PageRequestResolver extends AbstractListRequestResolver<PageRequest> {

	////////////////////////
	// VARIABLES
	////////////////

	public static final String OPTION_LIMIT = "pagerequest.pagesize";

	@Wired private StringToIntResolver intResolver;

	private ResolverOptions pagesSizeOptions = new ResolverOptions(RequestContextToStringResolver.OPTION_PARAMETER_NAME, this.getPagesSizeParameterName());
	private ResolverOptions pageNumberOptions = new ResolverOptions(RequestContextToStringResolver.OPTION_PARAMETER_NAME, this.getPageNumberParameterName());

	////////////////////////
	// METHODS
	////////////////

	@Override
	public PageRequest resolve(RequestContext requestContext, Class<?> expectedOutputType, ResolverContext resolverContext) {
		// Page size
		Integer pageSize = this.intResolver.resolve(this.getRequestParameter(requestContext, pagesSizeOptions), Integer.class, null);
		if (pageSize == null || pageSize < 0) {
			pageSize = resolverContext.getOptions().getInt(OPTION_LIMIT, DEFAULT_LIMIT);
		}

		// Page Number
		Integer pageNumber = this.intResolver.resolve(this.getRequestParameter(requestContext, pageNumberOptions), Integer.class, null);
		if (pageNumber == null || pageNumber < 0) {
			pageNumber = 1;
		}

		PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
		this.resolveSort(requestContext, pageRequest);
		return pageRequest;
	}

	////////////////////////
	// CUSTOMIZATION
	////////////////

	public String getPagesSizeParameterName() {
		return "pages_size";
	}

	public String getPageNumberParameterName() {
		return "page_number";
	}

}
