package co.mindie.cindy.resolver;

import co.mindie.cindy.AbstractCindyTest;
import co.mindie.cindy.automapping.Box;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.automapping.WiredCore;
import co.mindie.cindy.component.ComponentInitializer;
import co.mindie.cindy.component.ComponentMetadataManager;
import co.mindie.cindy.component.ComponentMetadataManagerBuilder;
import co.mindie.cindy.component.box.ComponentBox;
import co.mindie.cindy.exception.CindyException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Box(rejectAspects = {}, readOnly = false)
public class ResolverManagerTest extends AbstractCindyTest {
	@Wired private ResolverManager resolverManager;
	@WiredCore private ComponentBox componentBox;

	@Override
	protected void onLoad(ComponentMetadataManagerBuilder metadataManager) {
		super.onLoad(metadataManager);

		metadataManager.loadComponents("co.mindie.cindy");
	}

	private <Input, Output> Output resolve(Input input, Class<Output> outputClass) {
		return this.resolve(input, outputClass, (Class<Input>) input.getClass());
	}

	private <Input, Output> Output resolve(Input input, Class<Output> outputClass, Class<Input> inputClass) {
		return this.resolve(input, outputClass, this.resolverManager.getResolverOutput(inputClass, outputClass));
	}

	private <Input, Output> Output resolve(Input input, Class<Output> outputClass, IResolverBuilder builderOutput) {

		ComponentInitializer initializer = this.metadataManager.createInitializer();

		IResolver resolver = builderOutput.findOrCreateResolver(initializer, this.componentBox);

		initializer.init();

		return (Output) resolver.resolve(input, outputClass, null);
	}

	private <Input, Output> Output resolve(Input input) {
		return this.resolve(input, null, this.resolverManager.getDefaultResolverOutputForInput(input));
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

		this.metadataManagerBuilder.loadComponent(IntToMyObjectResolver.class);
		this.resolverManager.addConverter(IntToMyObjectResolver.class, Integer.class, MyObject.class, false, 0);

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

		this.metadataManagerBuilder.loadComponent(WeirdResolver.class);
		this.resolverManager.addConverter(WeirdResolver.class, Boolean.class, Object[].class, false, 0);

		output = this.resolverManager.getResolverOutput(Boolean.class, List.class);

		assertNotNull(output);

		List<Object> list = this.resolve(true, List.class, Boolean.class);

		assertTrue(list.size() == 1);
	}

	@Test
	public void addConverter_with_a_higher_priority_overrides_the_lower_priority() {
		this.metadataManagerBuilder.loadComponent(StringToInt1Resolver.class);
		this.metadataManagerBuilder.loadComponent(StringToInt2Resolver.class);

		this.resolverManager.addConverter(StringToInt1Resolver.class, String.class, Integer.class, false, 0);
		this.resolverManager.addConverter(StringToInt2Resolver.class, String.class, Integer.class, false, 1);

		IResolverBuilder output = this.resolverManager.getResolverOutput(String.class, Integer.class);

		assertNotNull(output);

		int value = this.resolve("", Integer.class);
		assertEquals(2, value);
	}

	@Test
	public void addConverter_with_the_same_priority_fails() {
		this.metadataManagerBuilder.loadComponent(StringToInt1Resolver.class);
		this.metadataManagerBuilder.loadComponent(StringToInt2Resolver.class);

		this.expectedException.expect(CindyException.class);

		this.resolverManager.addConverter(StringToInt1Resolver.class, String.class, Integer.class, false, 0);
		this.resolverManager.addConverter(StringToInt2Resolver.class, String.class, Integer.class, false, 0);
	}

	@Test
	public void addConverter_with_a_higher_priority_but_not_default_doesnt_override_the_default() {
		this.metadataManagerBuilder.loadComponent(StringToInt1Resolver.class);
		this.metadataManagerBuilder.loadComponent(StringToInt2Resolver.class);

		this.resolverManager.addConverter(StringToInt1Resolver.class, String.class, Integer.class, true, 0);
		this.resolverManager.addConverter(StringToInt2Resolver.class, String.class, Integer.class, false, 1);

		IResolverBuilder output = this.resolverManager.getResolverOutput(String.class, Integer.class);

		assertNotNull(output);

		int value = this.resolve("");
		assertEquals(1, value);
	}

	@Test
	public void addConverter_with_a_higher_priority_and_default_overrides_the_default() {
		this.metadataManagerBuilder.loadComponent(StringToInt1Resolver.class);
		this.metadataManagerBuilder.loadComponent(StringToInt2Resolver.class);

		this.resolverManager.addConverter(StringToInt1Resolver.class, String.class, Integer.class, true, 0);
		this.resolverManager.addConverter(StringToInt2Resolver.class, String.class, Integer.class, true, 1);

		IResolverBuilder output = this.resolverManager.getResolverOutput(String.class, Integer.class);

		assertNotNull(output);

		int value = this.resolve("");
		assertEquals(2, value);
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

	public static class StringToInt1Resolver implements IResolver<String, Integer> {
		@Override
		public Integer resolve(String input, Class<?> expectedOutputType, ResolverContext options) {
			return 1;
		}
	}

	public static class StringToInt2Resolver implements IResolver<String, Integer> {
		@Override
		public Integer resolve(String input, Class<?> expectedOutputType, ResolverContext options) {
			return 2;
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