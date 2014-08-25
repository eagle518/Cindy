/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// IParameterResolver.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2014 at 11:18:50 AM
////////

package co.mindie.cindy.controller.manager.entry;

import co.mindie.cindy.context.RequestContext;

public interface IParameterResolver {

	Object resolveParameter(RequestContext requestContext) throws Throwable;

}
