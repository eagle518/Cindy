package co.mindie.cindy.webservice.app;

import co.mindie.cindy.core.annotation.Box;
import co.mindie.cindy.core.annotation.MetadataModifier;
import co.mindie.cindy.core.component.Aspect;
import co.mindie.cindy.core.component.CreationBox;
import co.mindie.cindy.core.component.SearchScope;
import co.mindie.cindy.core.component.metadata.ComponentMetadata;
import co.mindie.cindy.core.component.metadata.ComponentMetadataManagerBuilder;
import co.mindie.cindy.webservice.annotation.Singleton;
import me.corsin.javatools.array.ArrayUtils;

@Box(rejectAspects = {})
public class SingletonManager {

	////////////////////////
	// VARIABLES
	////////////////


	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	@MetadataModifier
	public static void injectSingletons(ComponentMetadataManagerBuilder componentMetadataManagerBuilder) {
		for (ComponentMetadata appMetadata : componentMetadataManagerBuilder.findCompatibleComponentsForClass(SingletonManager.class)) {
			for (ComponentMetadata metadata : componentMetadataManagerBuilder.getLoadedComponentsWithAnnotation(Singleton.class)) {
				if (!ArrayUtils.arrayContains(metadata.getAspects(), Aspect.SINGLETON)) {
					metadata.setAspects(ArrayUtils.addItem(metadata.getAspects(), Aspect.SINGLETON));
				}
				if (!ArrayUtils.arrayContains(metadata.getAspects(), Aspect.THREAD_SAFE)) {
					metadata.setAspects(ArrayUtils.addItem(metadata.getAspects(), Aspect.THREAD_SAFE));
				}

				if (!appMetadata.hasDependency(metadata.getComponentClass())) {
					appMetadata.addDependency(metadata.getComponentClass(), true, false, SearchScope.GLOBAL, CreationBox.CURRENT_BOX);
				}
			}
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
