/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// IParameterResolver.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2014 at 11:18:50 AM
////////

package co.mindie.cindy.controller.manager.entry;

public interface IParameterResolver {

	Object resolveParameter(RequestHandler<?> requestHandler) throws Throwable;

}
