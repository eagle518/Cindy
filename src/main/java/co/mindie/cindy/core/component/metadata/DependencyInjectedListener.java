package co.mindie.cindy.core.component.metadata;

import co.mindie.cindy.core.component.metadata.ComponentDependency;
import co.mindie.cindy.core.component.metadata.ComponentMetadata;

/**
 * Created by simoncorsin on 20/11/14.
 */
public interface DependencyInjectedListener {

	void onComponentDependencyInjected(ComponentMetadata componentMetadata, ComponentDependency componentDependency, Object dependencyInstance);

}
