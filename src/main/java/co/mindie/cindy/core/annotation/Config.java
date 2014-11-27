/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// Service.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 4, 2014 at 1:42:02 PM
////////

package co.mindie.cindy.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Wire a config value from a key to a field.
 * Configuration values are stored using key-pair on {@link co.mindie.cindy.core.component.metadata.ComponentMetadata}.
 * If no configuration value with this key was found in the {@link co.mindie.cindy.core.component.metadata.ComponentMetadata}
 * and the component was loaded from a {@link co.mindie.cindy.core.module.Module}, an attempt to get this value will be
 * also done on the Module's {@link co.mindie.cindy.core.module.ModuleConfiguration} if useModuleConf() is true.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Config {

	/**
	 * @return the key to search in the {@link co.mindie.cindy.core.component.metadata.ComponentMetadata} configuration.
	 */
	String key();

	/**
	 * @return whether it should use the module configuration is no value with this key was found in the
	 * {@link co.mindie.cindy.core.component.metadata.ComponentMetadata}
	 */
	boolean useModuleConf() default true;

}
