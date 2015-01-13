package co.mindie.cindy.webservice.resolver.builtin;

import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.webservice.annotation.Resolver;
import co.mindie.cindy.core.annotation.Wired;
import co.mindie.cindy.webservice.context.RequestContext;
import co.mindie.cindy.dao.domain.OffsetedRequest;
import co.mindie.cindy.webservice.resolver.ResolverContext;
import co.mindie.cindy.webservice.resolver.ResolverOptions;

@Load(creationPriority = -1)
@Resolver(managedInputClasses = RequestContext.class, managedOutputClasses = OffsetedRequest.class)
public class OffsetedRequestResolver extends AbstractListRequestResolver<OffsetedRequest> {

	////////////////////////
	// VARIABLES
	////////////////

	public static final String OPTION_LIMIT = "offsetedrequest.limit";

	@Wired private StringToIntResolver intResolver;

	private ResolverOptions limitOptions = new ResolverOptions(RequestContextToStringResolver.OPTION_PARAMETER_NAME, this.getLimitParameterName());
	private ResolverOptions offsetOptions = new ResolverOptions(RequestContextToStringResolver.OPTION_PARAMETER_NAME, this.getOffsetParameterName());

	////////////////////////
	// METHODS
	////////////////

	@Override
	public OffsetedRequest resolve(RequestContext requestContext, Class<?> expectedOutputType, ResolverContext resolverContext) {

		// Limit
		Integer limit = this.intResolver.resolve(this.getRequestParameter(requestContext, this.limitOptions), Integer.class, null);
		if (limit == null || limit < 0) {
			limit = resolverContext.getOptions().getInt(OPTION_LIMIT, DEFAULT_LIMIT);
		}

		// Offset
		Integer offset = this.intResolver.resolve(this.getRequestParameter(requestContext, this.offsetOptions), Integer.class, null);
		if (offset == null || offset < 0) {
			offset = 0;
		}

		OffsetedRequest offsetedRequest = new OffsetedRequest(offset, limit);
		this.resolveSort(requestContext, offsetedRequest);
		return offsetedRequest;
	}

	////////////////////////
	// CUSTOMIZATION
	////////////////

	public String getLimitParameterName() {
		return "limit";
	}

	public String getOffsetParameterName() {
		return "offset";
	}
}
