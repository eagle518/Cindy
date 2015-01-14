package co.mindie.cindy.webservice.resolver;

import co.mindie.cindy.AbstractCindyTest;
import co.mindie.cindy.core.annotation.Box;
import co.mindie.cindy.webservice.resolver.batch.BatchOperator;
import me.corsin.javatools.misc.ValueHolder;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simoncorsin on 14/01/15.
 */
@Box(rejectAspects = {}, readOnly = false)
public class BatchResolverTest extends AbstractCindyTest {

	////////////////////////
	// VARIABLES
	////////////////

	public static class BatchOperatorTest implements BatchOperator<String, Integer> {

		@Override
		public List<Integer> doBatchOperation(List<String> operations) {
			List<Integer> ids = new ArrayList<>();

			operations.forEach(f -> ids.add(Integer.valueOf(f)));

			return ids;
		}
	}

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	@Test
	public void batch_resolver_flushes_correctly_and_is_in_order() {
		BatchOperatorTest batchOperator = new BatchOperatorTest();

		ResolverContext resolverContext = new ResolverContext();

		ValueHolder.IntegerHolder holder = new ValueHolder.IntegerHolder();
		holder.setValue(42);

		resolverContext.requestBatchOperation(batchOperator, "1", f ->  Assert.assertEquals(f.intValue(), 1));
		resolverContext.requestBatchOperation(batchOperator, "2", f ->  Assert.assertEquals(f.intValue(), 2));
		resolverContext.requestBatchOperation(batchOperator, "3", f ->  Assert.assertEquals(f.intValue(), 3));
		resolverContext.requestBatchOperation(batchOperator, "4", f ->  Assert.assertEquals(f.intValue(), 4));
		resolverContext.requestBatchOperation(batchOperator, "0", f -> holder.setValue(f));

		Assert.assertNotEquals(0, holder.value().intValue());

		resolverContext.flushBatchOperations();

		Assert.assertEquals(0, holder.value().intValue());
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
