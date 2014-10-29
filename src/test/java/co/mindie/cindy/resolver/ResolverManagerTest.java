package co.mindie.cindy.resolver;

import co.mindie.cindy.AbstractCindyTest;
import co.mindie.cindy.CindyWebApp;
import co.mindie.cindy.CindyWebAppCreator;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.automapping.WiredCore;
import co.mindie.cindy.component.ComponentBox;
import co.mindie.cindy.component.ComponentMetadataManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ResolverManagerTest extends AbstractCindyTest {

	@Wired private ResolverManager resolverManager;
	@WiredCore private ComponentBox componentBox;

	@Override
	protected void onLoad(ComponentMetadataManager metadataManager) {
		super.onLoad(metadataManager);

		metadataManager.loadComponents("co.mindie.cindy");
	}

	@Test
	public void resolve_string_to_int() {
		IResolverOutput output = this.resolverManager.getResolverOutput(String.class, int.class);
		int nb = (int) output.createResolversAndResolve(this.componentBox, "42", 0);

		assertTrue(nb == 42);
	}

	@Test
	public void resolve_nullstring_to_int() {
		IResolverOutput output = this.resolverManager.getResolverOutput(String.class, int.class);


		Exception ex = null;
		try {
			output.createResolversAndResolve(this.componentBox, null, 0);
		} catch (Exception e) {
			ex = e;
		}

		assertNotNull(ex);
	}

	@Test
	public void resolve_nullstring_to_integer() {
		IResolverOutput output = this.resolverManager.getResolverOutput(String.class, Integer.class);

		Integer it = (Integer) output.createResolversAndResolve(this.componentBox, null, 0);

		assertNull(it);
	}

	@Test
	public void resolve_string_to_longarray() {
		IResolverOutput output = this.resolverManager.getResolverOutput(String.class, long[].class);

		long[] array = (long[]) output.createResolversAndResolve(this.componentBox, "1;2;3;4;5;6;7;8", 0);

		assertNotNull(array);
		assertTrue(array.length == 8);
	}

	@Test
	public void resolve_string_to_longarray_with_commas() {
		IResolverOutput output = this.resolverManager.getResolverOutput(String.class, long[].class);

		long[] array = (long[]) output.createResolversAndResolve(this.componentBox, "1,2,3,4,5,6", 0);

		assertNotNull(array);
		assertTrue(array.length == 6);
	}

	@Test
	public void resolve_chained_simple() {
		IResolverOutput output = this.resolverManager.getResolverOutput(String.class, MyObject.class);

		assertNull(output);

		this.metadataManager.loadComponent(IntToMyObjectResolver.class);
		this.resolverManager.addConverter(IntToMyObjectResolver.class, Integer.class, MyObject.class, false);

		output = this.resolverManager.getResolverOutput(String.class, MyObject.class);

		assertNotNull(output);

		MyObject obj = (MyObject) output.createResolversAndResolve(this.componentBox, "1337", 0);

		assertTrue(obj.value == 1337);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void resolve_chained() {
		IResolverOutput output = this.resolverManager.getResolverOutput(Boolean.class, List.class);

		assertNull(output);

		this.metadataManager.loadComponent(WeirdResolver.class);
		this.resolverManager.addConverter(WeirdResolver.class, Boolean.class, Object[].class, false);

		output = this.resolverManager.getResolverOutput(Boolean.class, List.class);

		assertNotNull(output);

		List<Object> list = (List<Object>) output.createResolversAndResolve(this.componentBox, true, 0);

		assertTrue(list.size() == 1);
	}

	public static class MyObject {

		public int value;

	}

	public static class WeirdResolver implements IResolver<Boolean, Object[]> {
		@Override
		public Object[] resolve(Boolean input, Class<?> expectedOutputType, int options) {
			return new Object[]{new MyObject()};
		}
	}

	public static class IntToMyObjectResolver implements IResolver<Integer, MyObject> {

		@Override
		public MyObject resolve(Integer input, Class<?> expectedOutputType,
		                        int options) {
			MyObject obj = new MyObject();
			obj.value = input;

			return obj;
		}

	}
}