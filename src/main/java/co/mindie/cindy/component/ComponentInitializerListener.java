package co.mindie.cindy.component;

/**
 * Created by simoncorsin on 15/11/14.
 */
public interface ComponentInitializerListener {

	void onComponentCreated(ComponentInitializer initializer, CreatedComponent<?> createdComponent);

}
