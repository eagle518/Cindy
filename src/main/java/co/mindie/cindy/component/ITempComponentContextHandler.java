/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework
// ITempComponentContextHandler.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 17, 2014 at 7:14:39 PM
////////

package co.mindie.cindy.component;

import co.mindie.cindy.component.box.ComponentBox;

public interface ITempComponentContextHandler<T> {

	void handle(ComponentBox ctx, T object);

}
