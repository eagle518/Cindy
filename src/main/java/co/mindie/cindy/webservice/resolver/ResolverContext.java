/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver
// ModelConverterContext.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 6, 2014 at 1:53:33 PM
////////

package co.mindie.cindy.webservice.resolver;

import co.mindie.cindy.core.exception.CindyException;
import co.mindie.cindy.webservice.resolver.batch.BatchOperationResult;
import co.mindie.cindy.webservice.resolver.batch.BatchOperator;
import me.corsin.javatools.misc.NullArgumentException;
import me.corsin.javatools.misc.Pair;

import java.util.*;

public class ResolverContext {

	////////////////////////
	// VARIABLES
	////////////////

	private ResolverOptions options;
	private Map<Object, Object> cache;
	private Queue<Runnable> onCompletedCallbacks;
	private Map<BatchOperator, List<Pair<Object, BatchOperationResult>>> batchOperations;

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

	public void flushBatchOperations() {
		try {
			if (this.batchOperations != null) {
				for (Map.Entry<BatchOperator, List<Pair<Object, BatchOperationResult>>> entry : this.batchOperations.entrySet()) {
					BatchOperator batchOperator = entry.getKey();

					List<Object> values = new ArrayList<>();
					List<Pair<Object, BatchOperationResult>> resultPair = entry.getValue();
					resultPair.forEach(f -> values.add(f.getFirst()));

					List<Object> result = batchOperator.doBatchOperation(values);

					if (result.size() != values.size()) {
						throw new CindyException("BatchOperator must return a list that has the same size as the input" +
								" list and in the same order");
					}

					for (int i = 0; i < result.size(); i++) {
						resultPair.get(i).getSecond().onResult(result.get(i));
					}
				}
			}
		} catch (RuntimeException e) {
			throw e;
		} finally {
			this.cancelBatchOperations();
		}
	}

	public void cancelBatchOperations() {
		if (this.batchOperations != null) {
			this.batchOperations.clear();
		}
	}

	public <INPUT, OUTPUT> void requestBatchOperation(BatchOperator<INPUT, OUTPUT> batchOperator,
													  INPUT input,
													  BatchOperationResult<OUTPUT> onResult) {
		if (this.batchOperations == null) {
			this.batchOperations = new HashMap<>();
		}

		List<Pair<Object, BatchOperationResult>> batchOperations = this.batchOperations.get(batchOperator);

		if (batchOperations == null) {
			batchOperations = new ArrayList<>();
			this.batchOperations.put(batchOperator, batchOperations);
		}

		batchOperations.add(new Pair<>(input, onResult));
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public ResolverOptions getOptions() {
		return options;
	}

}
