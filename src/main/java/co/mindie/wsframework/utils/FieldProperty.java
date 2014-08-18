/////////////////////////////////////////////////
// Project : Mindie WebService
// Package : com.ever.wsframework.utils
// FieldProperty.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 5, 2013 at 1:07:14 PM
////////

package co.mindie.wsframework.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldProperty {

	int size();

}
