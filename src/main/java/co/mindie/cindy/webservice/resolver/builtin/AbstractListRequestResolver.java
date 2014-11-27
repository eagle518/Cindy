package co.mindie.cindy.webservice.resolver.builtin;

import co.mindie.cindy.core.annotation.Wired;
import co.mindie.cindy.webservice.context.RequestContext;
import co.mindie.cindy.dao.domain.AbstractListRequest;
import co.mindie.cindy.dao.domain.Direction;
import co.mindie.cindy.dao.domain.Sort;
import co.mindie.cindy.webservice.resolver.IResolver;
import co.mindie.cindy.webservice.resolver.ResolverContext;
import co.mindie.cindy.webservice.resolver.ResolverOptions;

public abstract class AbstractListRequestResolver<T extends AbstractListRequest> implements IResolver<RequestContext, T> {

	////////////////////////
	// VARIABLES
	////////////////

	public static int DEFAULT_LIMIT = 50;

	@Wired private RequestContextToStringResolver requestContextToStringResolver;
	@Wired private StringToBooleanResolver booleanResolver;

	private ResolverOptions reversedOptions = new ResolverOptions(RequestContextToStringResolver.OPTION_PARAMETER_NAME, this.getReversedParameterName());
	private ResolverOptions orderOptions = new ResolverOptions(RequestContextToStringResolver.OPTION_PARAMETER_NAME, this.getOrderParameterName());
	private ResolverOptions sortOptions = new ResolverOptions(RequestContextToStringResolver.OPTION_PARAMETER_NAME, this.getSortParameterName());
	private ResolverOptions propertyOptions = new ResolverOptions(RequestContextToStringResolver.OPTION_PARAMETER_NAME, this.getPropertyParameterName());

	////////////////////////
	// METHODS
	////////////////

	public T resolveSort(RequestContext requestContext, T listRequest) {
		// Sorting
		Sort sort = null;
		Boolean reverse = this.booleanResolver.resolve(this.getRequestParameter(requestContext, this.reversedOptions), Boolean.class, null);
		if (reverse == null) {
			String reverseStr = this.getRequestParameter(requestContext, this.orderOptions);
			if ("ASC".equalsIgnoreCase(reverseStr)) {
				reverse = false;
			}
		}
		String criteria = this.getRequestParameter(requestContext, this.propertyOptions);
		if (criteria == null) {
			criteria = this.getRequestParameter(requestContext, this.sortOptions);
		}
		if (reverse != null || criteria != null) {
			if (criteria == null) {
				criteria = "id";
			}
			if (reverse == null) {
				reverse = true;
			}
			sort = new Sort(reverse ? Direction.DESC : Direction.ASC, criteria);
		}

		if (sort != null) {
			listRequest.appendSort(sort);
		}
		return listRequest;
	}

	protected String getRequestParameter(RequestContext requestContext, ResolverOptions options) {
		return this.requestContextToStringResolver.resolve(requestContext, null, new ResolverContext(options));
	}

	////////////////////////
	// CUSTOMIZATION
	////////////////

	public String getReversedParameterName() {
		return "reversed";
	}

	public String getOrderParameterName() {
		return "order";
	}

	public String getSortParameterName() {
		return "sort";
	}

	public String getPropertyParameterName() {
		return "property";
	}
}
