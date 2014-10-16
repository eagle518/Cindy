package co.mindie.cindy.dao.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Request with pagination information.
 */
public abstract class AbstractListRequest {
	// //////////////////////
	// VARIABLES
	// //////////////

	private List<Sort> sorts;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public AbstractListRequest(AbstractListRequest original) {
		this(deepCopySorts(original.getSorts()));
	}

	private static List<Sort> deepCopySorts(List<Sort> sorts) {
		return sorts.stream()
				.map(sort -> new Sort(sort.getDirection(), sort.getProperty()))
				.collect(Collectors.toList());
	}

	public AbstractListRequest() {
		this(new ArrayList<>());
	}

	public AbstractListRequest(Direction direction, String propertyName) {
		this(new Sort(direction, propertyName));
	}

	public AbstractListRequest(Sort... sorts) {
		this(Arrays.asList(sorts));
	}

	public AbstractListRequest(List<Sort> sorts) {
		if (sorts == null) {
			this.sorts = new ArrayList<>();
		} else {
			this.sorts = sorts;
		}
	}

	// //////////////////////
	// ABSTRACT METHODS
	// //////////////

	/**
	 * Returns a new AbstractListRequest which is a copy of the current one, plus the given sort
	 * placed at the first place
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param sort the sort to prepend
	 * @return the modified @{link AbstractListRequest}
	 */
	public abstract AbstractListRequest withPrependedSort(Sort sort);

	/**
	 * Returns a new AbstractListRequest which is a copy of the current one, plus the given sort
	 * placed at the first place
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param direction    the direction of the sort
	 * @param propertyName the property name of the sort
	 * @return the modified @{link AbstractListRequest}
	 */
	public abstract AbstractListRequest withPrependedSort(Direction direction, String propertyName);

	/**
	 * Returns a new AbstractListRequest which is a copy of the current one, plus the given sort
	 * placed at the last place
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param sort the sort to append
	 * @return the modified @{link AbstractListRequest}
	 */
	public abstract AbstractListRequest withAppendedSort(Sort sort);

	/**
	 * Returns a new AbstractListRequest which is a copy of the current one, plus the given sort
	 * placed at the last place
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param direction    the direction of the sort
	 * @param propertyName the property name of the sort
	 * @return the modified @{link AbstractListRequest}
	 */
	public abstract AbstractListRequest withAppendedSort(Direction direction, String propertyName);

	/**
	 * Returns the AbstractListRequest that enables us to query the previous page.
	 * If the current page is the first one, it returns the current request.
	 *
	 * @return the previous @{link AbstractListRequest}
	 */
	public abstract AbstractListRequest previous();

	/**
	 * Returns the AbstractListRequest that enables us to query the next page.
	 *
	 * @return the next @{link AbstractListRequest}
	 */
	public abstract AbstractListRequest next();

	// //////////////////////
	// METHODS
	// //////////////

	/**
	 * Adds a @{link Sort} to the @{link AbstractListRequest} after the existing orders
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param sort the sort to add
	 */
	public void appendSort(Sort sort) {
		this.removeSortForProperty(sort.getProperty());
		this.sorts.add(sort);
	}

	/**
	 * Adds a @{link Sort} to the @{link AbstractListRequest} after the existing orders
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param direction    the direction of the sort
	 * @param propertyName the property name of the sort
	 */
	public void appendSort(Direction direction, String propertyName) {
		this.appendSort(new Sort(direction, propertyName));
	}

	/**
	 * Removes any @{link Sort} for the given property
	 *
	 * @param propertyName the property name
	 * @return the modified @{link AbstractListRequest}
	 */
	public AbstractListRequest removeSortForProperty(String propertyName) {
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
	 * Adds a @{link Sort} to the @{link AbstractListRequest} before the existing orders
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param sort the sort to add
	 */
	public void prependSort(Sort sort) {
		this.removeSortForProperty(sort.getProperty());
		if (this.sorts.size() > 0) {
			this.sorts.set(0, sort);
		} else {
			this.sorts.add(sort);
		}
	}

	/**
	 * Adds a @{link Sort} to the @{link AbstractListRequest} before the existing orders
	 * If a sort for this property already exists, it will be replaced by the new one
	 *
	 * @param direction    the direction of the sort
	 * @param propertyName the property name of the sort
	 */
	public void prependSort(Direction direction, String propertyName) {
		this.prependSort(new Sort(direction, propertyName));
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	/**
	 * The offset to be taken (number of items)
	 *
	 * @return the offset
	 */
	public abstract int getOffset();

	/**
	 * The number of items for each the page
	 *
	 * @return the number of items for this page
	 */
	public abstract int getLimit();

	/**
	 * Returns the sorting parameters
	 *
	 * @return the sorting parameters
	 */
	public List<Sort> getSorts() {
		return this.sorts;
	}

	/**
	 * Determine if the AbstractListRequest has sorts or not
	 *
	 * @return true if the AbstractListRequest has sorts, else false
	 */
	public boolean hasSort() {
		return this.sorts.size() > 0;
	}

	/**
	 * Determine if the AbstractListRequest has a sort for the given property
	 *
	 * @param propertyName the name of the property
	 * @return true if the AbstractListRequest has a sort for the given property, else false
	 */
	public boolean hasSort(String propertyName) {
		Iterator<Sort> ite = this.sorts.iterator();
		boolean hasSort = false;
		while (ite.hasNext() && !hasSort) {
			Sort current = ite.next();
			if (current.getProperty().equals(propertyName)) {
				hasSort = true;
			}
		}
		return hasSort;
	}
}