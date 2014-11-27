package co.mindie.cindy.core.component.box;

import co.mindie.cindy.core.component.Aspect;

public class ThreadSafeComponentBox extends ComponentBox {

	////////////////////////
	// VARIABLES
	////////////////

	final private Object lock = new Object();

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ThreadSafeComponentBox() {
		super();
	}

	public ThreadSafeComponentBox(Aspect[] neededAspects, Aspect[] rejectedAspects) {
		super(neededAspects, rejectedAspects);
	}

	public ThreadSafeComponentBox(Aspect[] neededAspects, Aspect[] rejectedAspects, ComponentBox superBox) {
		super(neededAspects, rejectedAspects, superBox);
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void init() {

	}

	public void addComponent(Object component, Aspect[] aspects) {
		synchronized (this.lock) {
			super.addComponent(component, aspects);
		}
	}

	@Override
	public void removeComponent(Object component) {
		synchronized (this.lock) {
			super.removeComponent(component);
		}
	}

	public void addChildBox(ComponentBox componentBox) {
		synchronized (this.lock) {
			super.addChildBox(componentBox);
		}
	}

	@Override
	public void removeChildBox(ComponentBox componentBox) {
		synchronized (this.lock) {
			super.removeChildBox(componentBox);
		}
	}


	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
