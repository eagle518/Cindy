/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.authentificator
// IRequestContextAuthorizer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2014 at 2:45:12 PM
////////

package co.mindie.cindy.webservice.authentificator;

import co.mindie.cindy.webservice.context.RequestContext;

public interface RequestContextAuthenticator {

	void authenticate(RequestContext context, String[] requiredAuthorizations) throws Exception;

}
