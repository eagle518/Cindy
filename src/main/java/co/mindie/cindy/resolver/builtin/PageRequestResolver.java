package co.mindie.cindy.resolver.builtin;

import co.mindie.cindy.automapping.Resolver;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.context.RequestContext;
import co.mindie.cindy.controller.manager.HttpRequest;
import co.mindie.cindy.controller.manager.RequestParameter;
import co.mindie.cindy.dao.domain.Direction;
import co.mindie.cindy.dao.domain.PageRequest;
import co.mindie.cindy.dao.domain.Sort;
import co.mindie.cindy.resolver.IResolver;
import org.apache.commons.fileupload.FileItem;

import java.util.List;

@Resolver(managedInputClasses = RequestParameter.class, managedOutputClasses = PageRequest.class)
public class PageRequestResolver implements IResolver<RequestParameter, PageRequest> {

	////////////////////////
	// VARIABLES
	////////////////

	public static int DEFAULT_LIMIT = 50;
	@Wired private RequestContext context;
	@Wired private StringToBooleanResolver booleanResolver;
	@Wired private StringToIntResolver intResolver;

	////////////////////////
	// METHODS
	////////////////

	@Override
	public PageRequest resolve(RequestParameter requestParameter, Class<?> expectedOutputType, int options) {

		// Limit
		Integer limit = this.intResolver.resolve(this.getRequestParameter("limit"), Integer.class, 0);
		if (limit == null) {
			if (options != 0) {
				limit = options;
			} else {
				limit = DEFAULT_LIMIT;
			}
		}

		// Offset
		Integer offset = this.intResolver.resolve(this.getRequestParameter("offset"), Integer.class, 0);
		if (offset == null) {
			offset = 0;
		}

		// Sorting
		Sort sort = null;
		Boolean reverse = this.booleanResolver.resolve(this.getRequestParameter("reversed"), Boolean.class, 0);
		String criteria = this.getRequestParameter("property");
		if (reverse != null || criteria != null) {
			if (criteria == null) {
				criteria = "id";
			}
			if (reverse == null) {
				reverse = true;
			}
			sort = new Sort(reverse ? Direction.DESC : Direction.ASC, criteria);
		}

		PageRequest pageRequest = new PageRequest(offset, limit);
		if (sort != null) {
			pageRequest.appendSort(sort);
		}
		return pageRequest;
	}

	private String getRequestParameter(String key) {
		HttpRequest httpRequest = this.context.getHttpRequest();
		String[] values = httpRequest.getQueryParameters().get(key);
		String value = null;

		if (values != null && values.length > 0) {
			value = values[0];
		} else {
			if (httpRequest.getBodyParameters() != null) {
				List<FileItem> bodyValues = httpRequest.getBodyParameters().get(key);
				if (bodyValues != null && bodyValues.size() > 0) {
					FileItem bodyValue = bodyValues.get(0);
					if (bodyValue.isFormField()) {
						value = bodyValue.getString();
					}
				}
			}
		}

		return value;
	}
}
