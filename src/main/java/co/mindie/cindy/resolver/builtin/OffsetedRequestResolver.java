package co.mindie.cindy.resolver.builtin;

import co.mindie.cindy.automapping.Resolver;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.controller.manager.RequestParameter;
import co.mindie.cindy.dao.domain.OffsetedRequest;

@Resolver(managedInputClasses = RequestParameter.class, managedOutputClasses = OffsetedRequest.class)
public class OffsetedRequestResolver extends AbstractListRequestResolver<OffsetedRequest> {

	////////////////////////
	// VARIABLES
	////////////////

	@Wired private StringToIntResolver intResolver;

	////////////////////////
	// METHODS
	////////////////

	@Override
	public OffsetedRequest resolve(RequestParameter requestParameter, Class<?> expectedOutputType, int options) {

		// Limit
		Integer limit = this.intResolver.resolve(this.getRequestParameter("limit"), Integer.class, 0);
		if (limit == null || limit < 0) {
			if (options != 0) {
				limit = options;
			} else {
				limit = DEFAULT_LIMIT;
			}
		}

		// Offset
		Integer offset = this.intResolver.resolve(this.getRequestParameter("offset"), Integer.class, 0);
		if (offset == null || offset < 0) {
			offset = 0;
		}

		OffsetedRequest offsetedRequest = new OffsetedRequest(offset, limit);
		this.resolveSort(offsetedRequest);
		return offsetedRequest;
	}
}
