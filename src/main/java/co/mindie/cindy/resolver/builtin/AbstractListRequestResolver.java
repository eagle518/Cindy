package co.mindie.cindy.resolver.builtin;

import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.context.RequestContext;
import co.mindie.cindy.controller.manager.HttpRequest;
import co.mindie.cindy.controller.manager.RequestParameter;
import co.mindie.cindy.dao.domain.AbstractListRequest;
import co.mindie.cindy.dao.domain.Direction;
import co.mindie.cindy.dao.domain.Sort;
import co.mindie.cindy.resolver.IResolver;
import org.apache.commons.fileupload.FileItem;

import java.util.List;

public abstract class AbstractListRequestResolver<T extends AbstractListRequest> implements IResolver<RequestParameter, T> {

	////////////////////////
	// VARIABLES
	////////////////

	public static int DEFAULT_LIMIT = 50;

	@Wired private RequestContext context;
	@Wired private StringToBooleanResolver booleanResolver;

	////////////////////////
	// METHODS
	////////////////

	public T resolveSort(T listRequest) {
		// Sorting
		Sort sort = null;
		Boolean reverse = this.booleanResolver.resolve(this.getRequestParameter("reversed"), Boolean.class, 0);
		if (reverse == null) {
			String reverseStr = this.getRequestParameter("order");
			if ("ASC".equalsIgnoreCase(reverseStr)) {
				reverse = false;
			}
		}
		String criteria = this.getRequestParameter("property");
		if (criteria == null) {
			criteria = this.getRequestParameter("sort");
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

	protected String getRequestParameter(String key) {
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
