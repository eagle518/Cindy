package co.mindie.cindy.core.module;

/**
 * Created by simoncorsin on 27/11/14.
 */
public interface Module {

	ModuleConfiguration getConfiguration();

	Class<?>[] getComponentClasses();

	String[] getComponentsClasspaths();

}
