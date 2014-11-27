/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.authorizer
// IRequestContextAuthorizer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2014 at 2:45:12 PM
////////

package co.mindie.cindy.webservice.authorizer;

import co.mindie.cindy.webservice.context.RequestContext;

public interface IRequestContextAuthorizer {

	void checkAuthorization(RequestContext context, String[] requiredAuthorizations) throws Exception;

}
