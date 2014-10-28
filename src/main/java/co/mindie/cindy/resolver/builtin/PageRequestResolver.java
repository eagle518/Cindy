package co.mindie.cindy.resolver.builtin;

import co.mindie.cindy.automapping.Load;
import co.mindie.cindy.automapping.Resolver;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.controller.manager.RequestParameter;
import co.mindie.cindy.dao.domain.PageRequest;

@Load
@Resolver(managedInputClasses = RequestParameter.class, managedOutputClasses = PageRequest.class)
public class PageRequestResolver extends AbstractListRequestResolver<PageRequest> {

	////////////////////////
	// VARIABLES
	////////////////

	@Wired private StringToIntResolver intResolver;

	////////////////////////
	// METHODS
	////////////////

	@Override
	public PageRequest resolve(RequestParameter requestParameter, Class<?> expectedOutputType, int options) {

		// Page size
		Integer pageSize = this.intResolver.resolve(this.getRequestParameter("pages_size"), Integer.class, 0);
		if (pageSize == null || pageSize < 0) {
			if (options != 0) {
				pageSize = options;
			} else {
				pageSize = DEFAULT_LIMIT;
			}
		}

		// Page Number
		Integer pageNumber = this.intResolver.resolve(this.getRequestParameter("page_number"), Integer.class, 0);
		if (pageNumber == null || pageNumber < 0) {
			pageNumber = 1;
		}

		PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
		this.resolveSort(pageRequest);
		return pageRequest;
	}
}
