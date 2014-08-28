package co.mindie.cindy.dao.domain;

/**
 * Sort options for queries.
 */
public class Sort {
	// //////////////////////
	// VARIABLES
	// //////////////

	private Direction direction;
	private String property;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public Sort(Direction direction, String property) {
		this.direction = direction;
		this.property = property;
	}

	// //////////////////////
	// METHODS
	// //////////////

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
}
