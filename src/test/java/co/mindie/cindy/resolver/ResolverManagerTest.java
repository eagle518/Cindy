package co.mindie.cindy.resolver;

import co.mindie.cindy.AbstractCindyTest;
import co.mindie.cindy.automapping.Box;
import co.mindie.cindy.automapping.Resolver;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.automapping.WiredCore;
import co.mindie.cindy.component.ComponentInitializer;
import co.mindie.cindy.component.box.ComponentBox;
import co.mindie.cindy.component.ComponentMetadataManager;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Box(rejectAspects = {}, readOnly = false)
public class ResolverManagerTest extends AbstractCindyTest {

	@Wired private ResolverManager resolverManager;
	@WiredCore private ComponentBox componentBox;

	@Override
	protected void onLoad(ComponentMetadataManager metadataManager) {
		super.onLoad(metadataManager);

		metadataManager.loadComponents("co.mindie.cindy");
	}

	private <Input, Output> Output resolve(Input input, Class<Output> outputClass) {
		return this.resolve(input, outputClass, (Class<Input>)input.getClass());
	}

	private <Input, Output> Output resolve(Input input, Class<Output> outputClass, Class<Input> inputClass) {
		IResolverBuilder output =  this.resolverManager.getResolverOutput(inputClass, outputClass);

		ComponentInitializer initializer = this.metadataManager.createInitializer();

		IResolver resolver = output.findOrCreateResolver(initializer, this.componentBox);

		initializer.init();

		return (Output)resolver.resolve(input, outputClass, null);
	}

	@Test
	public void resolve_string_to_int() {
		int nb = this.resolve("42", int.class);

		assertTrue(nb == 42);
	}

	@Test
	public void resolve_nullstring_to_int() {
		IResolverBuilder output = this.resolverManager.getResolverOutput(String.class, int.class);


		Exception ex = null;
		try {
			this.resolve(null, int.class, String.class);
		} catch (Exception e) {
			ex = e;
		}

		assertNotNull(ex);
	}

	@Test
	public void resolve_nullstring_to_integer() {
		Integer it = this.resolve(null, Integer.class, String.class);

		assertNull(it);
	}

	@Test
	public void resolve_string_to_longarray() {
		long[] array = this.resolve("1;2;3;4;5;6;7;8", long[].class);

		assertNotNull(array);
		assertTrue(array.length == 8);
	}

	@Test
	public void resolve_string_to_longarray_with_commas() {
		long[] array = this.resolve("1,2,3,4,5,6", long[].class);

		assertNotNull(array);
		assertTrue(array.length == 6);
	}

	@Test
	public void resolve_chained_simple() {
		IResolverBuilder output = this.resolverManager.getResolverOutput(String.class, MyObject.class);

		assertNull(output);

		this.metadataManager.loadComponent(IntToMyObjectResolver.class);
		this.resolverManager.addConverter(IntToMyObjectResolver.class, Integer.class, MyObject.class, false);

		output = this.resolverManager.getResolverOutput(String.class, MyObject.class);

		assertNotNull(output);

		MyObject obj = this.resolve("1337", MyObject.class);

		assertTrue(obj.value == 1337);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void resolve_chained() {
		IResolverBuilder output = this.resolverManager.getResolverOutput(Boolean.class, List.class);

		assertNull(output);

		this.metadataManager.loadComponent(WeirdResolver.class);
		this.resolverManager.addConverter(WeirdResolver.class, Boolean.class, Object[].class, false);

		output = this.resolverManager.getResolverOutput(Boolean.class, List.class);

		assertNotNull(output);

		List<Object> list = this.resolve(true, List.class, Boolean.class);

		assertTrue(list.size() == 1);
	}

	public static class MyObject {

		public int value;

	}

	public static class WeirdResolver implements IResolver<Boolean, Object[]> {
		@Override
		public Object[] resolve(Boolean input, Class<?> expectedOutputType, ResolverContext options) {
			return new Object[]{new MyObject()};
		}
	}

	public static class IntToMyObjectResolver implements IResolver<Integer, MyObject> {

		@Override
		public MyObject resolve(Integer input, Class<?> expectedOutputType,
		                        ResolverContext options) {
			MyObject obj = new MyObject();
			obj.value = input;

			return obj;
		}

	}
}