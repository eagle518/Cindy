package co.mindie.cindy.component.box;

import co.mindie.cindy.exception.CindyException;

/**
 * Created by simoncorsin on 29/10/14.
 */
public class BoxLockedException extends CindyException {

	////////////////////////
	// VARIABLES
	////////////////


	////////////////////////
	// CONSTRUCTORS
	////////////////

	public BoxLockedException(ComponentBox componentBox) {
		super("The component box " + componentBox + " is read only and cannot be altered once " +
				"it has been initialized.");
	}

	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
