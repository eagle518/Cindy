/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver
// IModelConverterOutput.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 28, 2014 at 4:58:07 PM
////////

package co.mindie.cindy.webservice.resolver;

import co.mindie.cindy.core.component.initializer.ComponentInitializer;
import co.mindie.cindy.core.component.box.ComponentBox;

public interface IResolverBuilder {

	IResolver findOrCreateResolver(ComponentInitializer initializer, ComponentBox componentBox);

}
