/////////////////////////////////////////////////
// Project : exiled-masterserver
// Package : com.kerious.exiled.masterserver.api
// Mapped.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 11, 2013 at 3:20:04 PM
////////

package co.mindie.cindy.automapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When added onto a static method, the method will be called before building the ComponentMetadataManager.
 * This method MUST be of prototype:
 * static void methodName(ComponentMetadataManagerBuilder metadataManagerBuilder)
 * This can be used to do some further operations on the ComponentMetadataManagerBuilder before it builds
 * the final ComponentMetadataManager.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MetadataModifier {

}
