package co.mindie.cindy.component;

/**
 * Created by simoncorsin on 20/11/14.
 */
public interface DependencyInjectedListener {

	void onComponentDependencyInjected(ComponentMetadata componentMetadata, ComponentDependency componentDependency, Object dependencyInstance);

}
