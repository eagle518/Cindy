package co.mindie.cindy.dao.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PageRequest extends AbstractListRequest {
	// //////////////////////
	// VARIABLES
	// //////////////

	private int pageNumber;
	private int pageSize;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public PageRequest(PageRequest original) {
		super(original);
		this.pageNumber = original.getPageNumber();
		this.pageSize = original.getPageSize();
	}

	public PageRequest(int pageNumber, int pageSize) {
		this(pageNumber, pageSize, new ArrayList<>());
	}

	public PageRequest(int pageNumber, int pageSize, Direction direction, String propertyName) {
		this(pageNumber, pageSize, new Sort(direction, propertyName));
	}

	public PageRequest(int pageNumber, int pageSize, Sort... sorts) {
		this(pageNumber, pageSize, Arrays.asList(sorts));
	}

	public PageRequest(int pageNumber, int pageSize, List<Sort> sorts) {
		super(sorts);
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}

	// //////////////////////
	// METHODS
	// //////////////

	@Override
	public PageRequest withPrependedSort(Sort sort) {
		PageRequest pageRequest = new PageRequest(this);
		pageRequest.prependSort(sort);
		return pageRequest;
	}

	@Override
	public PageRequest withPrependedSort(Direction direction, String propertyName) {
		return this.withPrependedSort(new Sort(direction, propertyName));
	}

	@Override
	public PageRequest withAppendedSort(Sort sort) {
		PageRequest pageRequest = new PageRequest(this);
		pageRequest.appendSort(sort);
		return pageRequest;
	}

	@Override
	public PageRequest withAppendedSort(Direction direction, String propertyName) {
		return this.withAppendedSort(new Sort(direction, propertyName));
	}

	@Override
	public PageRequest previous() {
		int newPageNumber = this.pageNumber - 1;
		if (newPageNumber < 0) {
			newPageNumber = 1;
		}
		return new PageRequest(newPageNumber, this.pageSize, this.getSorts());
	}

	@Override
	public PageRequest next() {
		return new PageRequest(this.pageNumber + 1, this.pageSize, this.getSorts());
	}

	// //////////////////////
	// METHODS
	// //////////////

	@Override
	public int getOffset() {
		return (this.pageNumber - 1) * this.pageSize;
	}

	@Override
	public int getLimit() {
		return this.pageSize;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
