/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.resolver
// IModelConverter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 20, 2014 at 5:42:24 PM
////////

package co.mindie.cindy.webservice.resolver;

public interface IResolver<Input, Output> {

	Output resolve(Input input, Class<?> expectedOutputType, ResolverContext resolverContext);

}