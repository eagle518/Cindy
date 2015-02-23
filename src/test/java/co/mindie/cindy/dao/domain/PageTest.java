package co.mindie.cindy.dao.domain;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class PageTest {
	private static final List<Sort> SORTS = Lists.newArrayList(
			new Sort(Direction.ASC, "created_date"),
			new Sort(Direction.DESC, "test")
	);

	@Test
	public void hasPrevious_if_pageRequest_null_returns_false() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3));

		// WHEN
		boolean result = page.hasPrevious();

		// THEN
		assertFalse(result);
	}

	@Test
	public void hasPrevious_if_offset_not_0_and_totalElements_not_0_returns_true() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3), new OffsetedRequest(1, 10), 3);

		// WHEN
		boolean result = page.hasPrevious();

		// THEN
		assertTrue(result);
	}

	@Test
	public void hasPrevious_if_offset_not_0_and_totalElements_0_returns_false() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3), new OffsetedRequest(1, 10), 0);

		// WHEN
		boolean result = page.hasPrevious();

		// THEN
		assertFalse(result);
	}

	@Test
	public void hasPrevious_if_offset_0_returns_false() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3), new OffsetedRequest(0, 10), 3);

		// WHEN
		boolean result = page.hasPrevious();

		// THEN
		assertFalse(result);
	}

	@Test
	public void previousPageRequest_if_pageRequest_null_returns_null() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3));

		// WHEN
		AbstractListRequest result = page.previousPageRequest();

		// THEN
		assertNull(result);
	}

	@Test
	public void previousPageRequest_if_hasPrevious_false_returns_null() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3), new OffsetedRequest(0, 1), 10);

		// WHEN
		AbstractListRequest result = page.previousPageRequest();

		// THEN
		assertNull(result);
	}

	@Test
	public void previousPageRequest_if_hasPrevious_true_returns_an_offset_decreased_by_the_limit() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3), new OffsetedRequest(8, 1, SORTS), 10);

		// WHEN
		AbstractListRequest result = page.previousPageRequest();

		// THEN
		assertEquals(SORTS, result.getSorts());
		assertEquals(1, result.getLimit().intValue());
		assertEquals(7, result.getOffset().intValue());
	}

	@Test
	public void hasNext_if_pageRequest_null_returns_false() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3));

		// WHEN
		boolean result = page.hasNext();

		// THEN
		assertFalse(result);
	}

	@Test
	public void hasNext_if_offset_plus_limit_lower_than_totalElements_returns_true() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3), new OffsetedRequest(0, 10), 11);

		// WHEN
		boolean result = page.hasNext();

		// THEN
		assertTrue(result);
	}

	@Test
	public void hasNext_if_offset_plus_limit_equals_totalElements_returns_false() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3), new OffsetedRequest(2, 10), 12);

		// WHEN
		boolean result = page.hasNext();

		// THEN
		assertFalse(result);
	}

	@Test
	public void hasNext_if_offset_plus_limit_greater_than_totalElements_returns_false() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3), new OffsetedRequest(2, 11), 12);

		// WHEN
		boolean result = page.hasNext();

		// THEN
		assertFalse(result);
	}

	@Test
	public void nextPageRequest_if_pageRequest_null_returns_null() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3));

		// WHEN
		AbstractListRequest result = page.nextPageRequest();

		// THEN
		assertNull(result);
	}

	@Test
	public void nextPageRequest_if_hasNext_false_returns_null() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3), new OffsetedRequest(0, 100), 10);

		// WHEN
		AbstractListRequest result = page.nextPageRequest();

		// THEN
		assertNull(result);
	}

	@Test
	public void nextPageRequest_if_hasNext_true_returns_an_offset_increased_by_the_limit() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3), new OffsetedRequest(8, 1, SORTS), 100);

		// WHEN
		AbstractListRequest result = page.nextPageRequest();

		// THEN
		assertEquals(SORTS, result.getSorts());
		assertEquals(1, result.getLimit().intValue());
		assertEquals(9, result.getOffset().intValue());
	}

	@Test
	public void getTotalElements_if_not_specified_in_constructor_returns_the_page_size() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3));

		// WHEN
		long result = page.getTotalElements();

		// THEN
		assertEquals(3, result);
	}

	@Test
	public void getTotalElements_if_specified_in_constructor_returns_the_totalElements() {
		// GIVEN
		Page<MyObj> page = new Page<>(generateObjects(3), null, 8);

		// WHEN
		long result = page.getTotalElements();

		// THEN
		assertEquals(8, result);
	}

	// //////////////////////
	// TOOLS
	// //////////////

	private static class MyObj {
	}

	private static List<MyObj> generateObjects(int num) {
		List<MyObj> objects = new ArrayList<>(num);
		for (int i = 0; i < num; i++) {
			objects.add(new MyObj());
		}
		return objects;
	}
}