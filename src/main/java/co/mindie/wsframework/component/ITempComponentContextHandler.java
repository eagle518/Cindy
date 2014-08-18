/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// ITempComponentContextHandler.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 17, 2014 at 7:14:39 PM
////////

package co.mindie.wsframework.component;

public interface ITempComponentContextHandler<T> {

	void handle(ComponentContext ctx, T object);

}
