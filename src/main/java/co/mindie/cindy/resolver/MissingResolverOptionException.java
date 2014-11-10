package co.mindie.cindy.resolver;

import co.mindie.cindy.exception.CindyException;

public class MissingResolverOptionException extends CindyException {

	////////////////////////
	// VARIABLES
	////////////////


	////////////////////////
	// CONSTRUCTORS
	////////////////

	public MissingResolverOptionException(String optionName) {
		super("Cannot resolve: Missing resolver option [" + optionName + "]");
	}

	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
