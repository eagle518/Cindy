package co.mindie.cindy.dao.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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

	public PageRequest(PageRequest original) {
		this(original.getOffset(), original.getLimit(), new ArrayList<>(original.getSorts()));
	}

	public PageRequest(int offset, int limit) {
		this(offset, limit, new ArrayList<>());
	}

	public PageRequest(int offset, int limit, Direction direction, String propertyName) {
		this(offset, limit, new Sort(direction, propertyName));
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
	 * Adds a @{link Sort} to the @{link PageRequest} after the existing orders
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param sort the sort to add
	 * @return the modified @{link PageRequest}
	 */
	public PageRequest appendSort(Sort sort) {
		this.removeSortForProperty(sort.getProperty());
		this.sorts.add(sort);
		return this;
	}

	/**
	 * Adds a @{link Sort} to the @{link PageRequest} after the existing orders
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param direction    the direction of the sort
	 * @param propertyName the property name of the sort
	 * @return the modified @{link PageRequest}
	 */
	public PageRequest appendSort(Direction direction, String propertyName) {
		return this.appendSort(new Sort(direction, propertyName));
	}

	/**
	 * Removes any @{link Sort} for the given property
	 *
	 * @param propertyName the property name
	 * @return the modified @{link PageRequest}
	 */
	public PageRequest removeSortForProperty(String propertyName) {
		Iterator<Sort> ite = this.sorts.iterator();
		while (ite.hasNext()) {
			Sort current = ite.next();
			if (current.getProperty().equals(propertyName)) {
				ite.remove();
			}
		}
		return this;
	}

	/**
	 * Adds a @{link Sort} to the @{link PageRequest} before the existing orders
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param sort the sort to add
	 * @return the modified @{link PageRequest}
	 */
	public PageRequest prependSort(Sort sort) {
		this.removeSortForProperty(sort.getProperty());
		this.sorts.set(0, sort);
		return this;
	}

	/**
	 * Adds a @{link Sort} to the @{link PageRequest} before the existing orders
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param direction    the direction of the sort
	 * @param propertyName the property name of the sort
	 * @return the modified @{link PageRequest}
	 */
	public PageRequest prependSort(Direction direction, String propertyName) {
		return this.prependSort(new Sort(direction, propertyName));
	}

	/**
	 * Returns a new PageRequest which is a copy of the current one, plus the given sort
	 * placed at the first place
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param sort the sort to prepend
	 * @return the modified @{link PageRequest}
	 */
	public PageRequest withPrependedSort(Sort sort) {
		return new PageRequest(this).prependSort(sort);
	}

	/**
	 * Returns a new PageRequest which is a copy of the current one, plus the given sort
	 * placed at the first place
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param direction    the direction of the sort
	 * @param propertyName the property name of the sort
	 * @return the modified @{link PageRequest}
	 */
	public PageRequest withPrependedSort(Direction direction, String propertyName) {
		return this.withPrependedSort(new Sort(direction, propertyName));
	}

	/**
	 * Returns a new PageRequest which is a copy of the current one, plus the given sort
	 * placed at the last place
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param sort the sort to append
	 * @return the modified @{link PageRequest}
	 */
	public PageRequest withAppendedSort(Sort sort) {
		return new PageRequest(this).appendSort(sort);
	}

	/**
	 * Returns a new PageRequest which is a copy of the current one, plus the given sort
	 * placed at the last place
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param direction    the direction of the sort
	 * @param propertyName the property name of the sort
	 * @return the modified @{link PageRequest}
	 */
	public PageRequest withAppendedSort(Direction direction, String propertyName) {
		return this.withAppendedSort(new Sort(direction, propertyName));
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

	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * The number of items for each the page
	 *
	 * @return the number of items for this page
	 */
	public int getLimit() {
		return this.limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * Returns the sorting parameters
	 *
	 * @return the sorting parameters
	 */
	public List<Sort> getSorts() {
		return this.sorts;
	}

	/**
	 * Determine if the PageRequest has sorts or not
	 *
	 * @return true if the PageRequest has sorts, else false
	 */
	public boolean hasSort() {
		return this.sorts.size() > 0;
	}
}
