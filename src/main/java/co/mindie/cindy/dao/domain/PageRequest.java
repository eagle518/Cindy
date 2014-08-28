package co.mindie.cindy.dao.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Request with pagination information.
 */
public class PageRequest {
	// //////////////////////
	// VARIABLES
	// //////////////

	private int offset;
	private int limit;
	private List<Sort> sorts;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public PageRequest(int offset, int limit) {
		this(offset, limit, new ArrayList<>());
	}

	public PageRequest(int offset, int limit, Sort... sorts) {
		this(offset, limit, Arrays.asList(sorts));
	}

	public PageRequest(int offset, int limit, List<Sort> sorts) {
		this.offset = offset;
		this.limit = limit;

		if (sorts == null) {
			this.sorts = new ArrayList<>();
		} else {
			this.sorts = sorts;
		}
	}

	// //////////////////////
	// METHODS
	// //////////////

	/**
	 * Adds a @{link Sort} to the @{link PageRequest}
	 *
	 * @return the modified @{link PageRequest}
	 */
	public PageRequest addSort(Sort sort) {
		this.sorts.add(sort);
		return this;
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	/**
	 * The offset to be taken (number of items)
	 *
	 * @return the offset
	 */
	public int getOffset() {
		return this.offset;
	}

	/**
	 * The number of items for each the page
	 *
	 * @return the number of items for this page
	 */
	public int getLimit() {
		return this.limit;
	}


	/**
	 * Returns the sorting parameters
	 *
	 * @return the sorting parameters
	 */
	public List<Sort> getSorts() {
		return this.sorts;
	}
}
