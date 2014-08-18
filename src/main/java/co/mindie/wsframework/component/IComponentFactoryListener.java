package co.mindie.wsframework.component;

public interface IComponentFactoryListener {

	void onComponentCreated(Object componentInstance);

	void onComponentInitialized(Object componentInstance);

	void onComponentInitializationFailed(Object componentInstance, Exception e);

}
