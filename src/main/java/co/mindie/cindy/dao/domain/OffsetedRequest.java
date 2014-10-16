package co.mindie.cindy.dao.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Trololo with offset and limit information.
 */
public class OffsetedRequest extends AbstractListRequest {
	// //////////////////////
	// VARIABLES
	// //////////////

	private int offset;
	private int limit;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public OffsetedRequest(OffsetedRequest original) {
		super(original);
		this.offset = original.getOffset();
		this.limit = original.getLimit();
	}

	public OffsetedRequest(int offset, int limit) {
		this(offset, limit, new ArrayList<>());
	}

	public OffsetedRequest(int offset, int limit, Direction direction, String propertyName) {
		this(offset, limit, new Sort(direction, propertyName));
	}

	public OffsetedRequest(int offset, int limit, Sort... sorts) {
		this(offset, limit, Arrays.asList(sorts));
	}

	public OffsetedRequest(int offset, int limit, List<Sort> sorts) {
		super(sorts);
		this.offset = offset;
		this.limit = limit;
	}

	// //////////////////////
	// METHODS
	// //////////////

	@Override
	public OffsetedRequest withPrependedSort(Sort sort) {
		OffsetedRequest newRequest = new OffsetedRequest(this);
		newRequest.prependSort(sort);
		return newRequest;
	}

	@Override
	public OffsetedRequest withPrependedSort(Direction direction, String propertyName) {
		return this.withPrependedSort(new Sort(direction, propertyName));
	}

	@Override
	public OffsetedRequest withAppendedSort(Sort sort) {
		OffsetedRequest newRequest = new OffsetedRequest(this);
		newRequest.appendSort(sort);
		return newRequest;
	}

	@Override
	public OffsetedRequest withAppendedSort(Direction direction, String propertyName) {
		return this.withAppendedSort(new Sort(direction, propertyName));
	}

	@Override
	public OffsetedRequest previous() {
		int newOffset = this.offset - this.limit;
		if (newOffset < 0) {
			newOffset = 0;
		}
		return new OffsetedRequest(newOffset, this.limit, this.getSorts());
	}

	@Override
	public OffsetedRequest next() {
		int newOffset = this.offset + this.limit;
		return new OffsetedRequest(newOffset, this.limit, this.getSorts());
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	/**
	 * The offset to be taken (number of items)
	 *
	 * @return the offset
	 */
	@Override
	public int getOffset() {
		return this.offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * The number of items for each the page
	 *
	 * @return the number of items for this page
	 */
	@Override
	public int getLimit() {
		return this.limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}
