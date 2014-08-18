/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.authorizer
// RequestContextAuthorizer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 9, 2014 at 10:41:27 PM
////////

package co.mindie.wsframework.authorizer;

import co.mindie.wsframework.component.WSComponent;
import co.mindie.wsframework.context.RequestContext;

public abstract class WSRequestContextAuthorizer extends WSComponent implements IRequestContextAuthorizer {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public WSRequestContextAuthorizer() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public abstract void checkAuthorization(RequestContext context, String[] requiredAuthorizations) throws Exception;

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
