package co.mindie.cindy.dao.domain;

import co.mindie.cindy.exception.CindyException;

import java.util.Iterator;
import java.util.List;

/**
 * A Page is a sublist of a list of objects. It also gives informations of the position of this sublist
 * in the full list.
 */
public class Page<T> implements Iterable<T> {
	// //////////////////////
	// VARIABLES
	// //////////////

	private List<T> data;
	private long totalElements;
	private PageRequest pageRequest;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public Page(List<T> data) {
		this(data, null, data.size());
	}

	public Page(List<T> data, PageRequest pageRequest, long totalElements) {
		if (data == null) {
			throw new CindyException("Null data");
		}
		this.data = data;
		this.pageRequest = pageRequest;
		this.totalElements = totalElements;
	}

	// //////////////////////
	// METHODS
	// //////////////

	public boolean hasPrevious() {
		return this.pageRequest != null
				&& this.pageRequest.getOffset() != 0
				&& this.totalElements != 0;
	}

	public PageRequest previousPageRequest() {
		if (this.hasPrevious()) {
			return new PageRequest(
					this.pageRequest.getOffset() - this.pageRequest.getLimit(),
					this.pageRequest.getLimit(),
					this.pageRequest.getSorts()
			);
		}
		return null;
	}

	public boolean hasNext() {
		return this.pageRequest != null
				&& this.pageRequest.getOffset() + this.pageRequest.getLimit() < this.totalElements;
	}

	public PageRequest nextPageRequest() {
		if (this.hasNext()) {
			return new PageRequest(
					this.pageRequest.getOffset() + this.pageRequest.getLimit(),
					this.pageRequest.getLimit(),
					this.pageRequest.getSorts()
			);
		}
		return null;
	}

	// //////////////////////
	// DATA ENCAPSULATION
	// //////////////

	@Override
	public Iterator<T> iterator() {
		return this.data.iterator();
	}

	public T get(int index) {
		return this.data.get(index);
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	public List<T> getData() {
		return this.data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public PageRequest getPageRequest() {
		return pageRequest;
	}

	public void setPageRequest(PageRequest pageRequest) {
		this.pageRequest = pageRequest;
	}

	public int getSize() {
		return this.data.size();
	}

	public long getTotalElements() {
		return this.totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}
}
