/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.modelconverter
// IModelConverter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 20, 2014 at 5:42:24 PM
////////

package co.mindie.wsframework.modelconverter;

public interface IResolver<Input, Output> {

	Output resolve(Input input, Class<?> expectedOutputType, int options);

}