/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// RequestParameterCreator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2014 at 11:18:50 AM
////////

package co.mindie.cindy.controller.manager.entry;

import co.mindie.cindy.controller.manager.RequestParameter;

public interface RequestParameterCreator {

	RequestParameter createParameter(RequestHandler<?> requestHandler) throws Throwable;

}
