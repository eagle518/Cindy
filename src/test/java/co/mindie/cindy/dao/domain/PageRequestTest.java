package co.mindie.cindy.dao.domain;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class PageRequestTest {
	@Test
	public void appendSort_appends_the_sort_a_the_end_of_the_list() {
		// GIVEN
		Sort sort1 = new Sort(Direction.ASC, "created_date");
		Sort sort2 = new Sort(Direction.DESC, "updated_date");
		List<Sort> sorts = Lists.newArrayList(sort1, sort2);

		PageRequest pageRequest = new PageRequest(0, 10, sorts);

		// WHEN
		Sort sort = new Sort(Direction.ASC, "test");
		pageRequest.appendSort(sort);

		// THEN
		assertEquals(3, pageRequest.getSorts().size());
		assertEquals(sort, pageRequest.getSorts().get(2));
	}
}