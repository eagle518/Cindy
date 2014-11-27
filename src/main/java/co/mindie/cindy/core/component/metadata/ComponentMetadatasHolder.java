package co.mindie.cindy.core.component.metadata;

import co.mindie.cindy.core.component.metadata.ComponentMetadata;
import co.mindie.cindy.core.exception.CindyException;
import me.corsin.javatools.reflect.ClassIndexer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by simoncorsin on 19/11/14.
 */
public class ComponentMetadatasHolder {

	////////////////////////
	// VARIABLES
	////////////////

	final protected Map<Class<?>, ComponentMetadata> metadatas;
	final protected ClassIndexer<ComponentMetadata> componentIndexer;
	final protected Map<Class<?>, List<ComponentMetadata>> metadatasByAnnotation;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ComponentMetadatasHolder(Map<Class<?>, ComponentMetadata> metadatas, ClassIndexer<ComponentMetadata> componentIndexer,
									Map<Class<?>, List<ComponentMetadata>> metadatasByAnnotation) {
		this.metadatas = metadatas;
		this.componentIndexer = componentIndexer;
		this.metadatasByAnnotation = metadatasByAnnotation;
	}

	////////////////////////
	// METHODS
	////////////////

	public ComponentMetadata getComponentMetadata(Class<?> objectClass) {
		return this.metadatas.get(objectClass);
	}


	public ComponentMetadata findCompatibleComponentForClass(Class<?> objectClass) {
		List<ComponentMetadata> componentMetadatas = this.findCompatibleComponentsForClass(objectClass);

		if (componentMetadatas == null) {
			return null;
		}

		if (componentMetadatas.size() > 1) {
			throw new CindyException("More than one component is compatible for this type " + objectClass);
		}

		return componentMetadatas.get(0);
	}

	public List<ComponentMetadata> findCompatibleComponentsForClass(Class<?> objectClass) {
		return this.componentIndexer.find(objectClass);
	}

	/**
	 * @param annotationType The annotation type to search
	 * @return A list containing all the component metadata that has the annotationType
	 */
	public <T extends Annotation> List<ComponentMetadata> getLoadedComponentsWithAnnotation(Class<T> annotationType) {
		List<ComponentMetadata> metadatas = this.metadatasByAnnotation.get(annotationType);

		if (metadatas == null) {
			metadatas = new ArrayList<>();
		}

		return metadatas;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Collection<ComponentMetadata> getLoadedComponentMetadatas() {
		return this.metadatas.values();
	}
}
