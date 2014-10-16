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
	private long totalPages;
	private AbstractListRequest listRequest;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public Page(List<T> data) {
		this(data, null, data.size());
	}

	public Page(List<T> data, AbstractListRequest listRequest, long totalElements) {
		if (data == null) {
			throw new CindyException("Null data");
		}
		this.data = data;
		this.listRequest = listRequest;
		this.totalElements = totalElements;
		if (listRequest instanceof PageRequest) {
			this.totalPages = totalElements / listRequest.getLimit() + 1;
		}
	}

	// //////////////////////
	// METHODS
	// //////////////

	public boolean hasPrevious() {
		return this.listRequest != null
				&& this.listRequest.getOffset() != 0
				&& this.totalElements != 0;
	}

	public AbstractListRequest previousPageRequest() {
		if (this.hasPrevious()) {
			return this.listRequest.previous();
		}
		return null;
	}

	public boolean hasNext() {
		return this.listRequest != null
				&& this.listRequest.getOffset() + this.listRequest.getLimit() < this.totalElements;
	}

	public AbstractListRequest nextPageRequest() {
		if (this.hasNext()) {
			return this.listRequest.next();
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

	public AbstractListRequest getListRequest() {
		return listRequest;
	}

	public void setListRequest(AbstractListRequest listRequest) {
		this.listRequest = listRequest;
	}

	public int size() {
		return this.data.size();
	}

	public long getTotalElements() {
		return this.totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public long getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}
}
