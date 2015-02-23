package co.mindie.cindy.dao.domain;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class AbstractListRequestTest {
	@Test
	public void appendSort_appends_the_sort_a_the_end_of_the_list() {
		// GIVEN
		Sort sort1 = new Sort(Direction.ASC, "created_date");
		Sort sort2 = new Sort(Direction.DESC, "updated_date");
		List<Sort> sorts = Lists.newArrayList(sort1, sort2);

		AbstractListRequest abstractListRequest = new DummyListRequest(sorts);

		// WHEN
		Sort sort = new Sort(Direction.ASC, "test");
		abstractListRequest.appendSort(sort);

		// THEN
		assertEquals(3, abstractListRequest.getSorts().size());
		assertEquals(sort, abstractListRequest.getSorts().get(2));
	}

	public class DummyListRequest extends AbstractListRequest {
		public DummyListRequest(List<Sort> sorts) {
			super(sorts);
		}

		@Override
		public AbstractListRequest withPrependedSort(Sort sort) {
			return null;
		}

		@Override
		public AbstractListRequest withPrependedSort(Direction direction, String propertyName) {
			return null;
		}

		@Override
		public AbstractListRequest withAppendedSort(Sort sort) {
			return null;
		}

		@Override
		public AbstractListRequest withAppendedSort(Direction direction, String propertyName) {
			return null;
		}

		@Override
		public AbstractListRequest previous() {
			return null;
		}

		@Override
		public AbstractListRequest next() {
			return null;
		}

		@Override
		public Integer getOffset() {
			return 0;
		}

		@Override
		public Integer getLimit() {
			return 0;
		}
	}
}