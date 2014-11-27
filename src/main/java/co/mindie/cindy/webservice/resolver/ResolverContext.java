/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver
// ModelConverterContext.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 6, 2014 at 1:53:33 PM
////////

package co.mindie.cindy.webservice.resolver;

import me.corsin.javatools.misc.NullArgumentException;

import java.util.HashMap;
import java.util.Map;

public class ResolverContext {

	////////////////////////
	// VARIABLES
	////////////////

	private ResolverOptions options;
	private Map<Object, Object> cache;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ResolverContext() {
		this(ResolverOptions.emptyOptions());
	}

	public ResolverContext(ResolverOptions resolverOptions) {
		if (resolverOptions == null) {
			throw new NullArgumentException("resolverOptions");
		}

		this.options = resolverOptions;
	}

	////////////////////////
	// METHODS
	////////////////

	public void putCache(Object id, Object object) {
		if (this.cache == null) {
			this.cache = new HashMap<>();
		}

		this.cache.put(id, object);
	}

	public Object getCached(Object id) {
		if (this.cache == null) {
			return null;
		}

		return this.cache.get(id);
	}

	public void clearCache() {
		if (this.cache != null) {
			this.cache.clear();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public ResolverOptions getOptions() {
		return options;
	}
}
