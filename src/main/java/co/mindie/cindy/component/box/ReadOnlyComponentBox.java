package co.mindie.cindy.component.box;

import co.mindie.cindy.component.ComponentAspect;

public class ReadOnlyComponentBox extends ComponentBox {

	////////////////////////
	// VARIABLES
	////////////////

	private volatile boolean locked;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ReadOnlyComponentBox() {
		super();
	}

	public ReadOnlyComponentBox(ComponentAspect[] neededAspects, ComponentAspect[] rejectedAspects) {
		super(neededAspects, rejectedAspects);
	}

	public ReadOnlyComponentBox(ComponentAspect[] neededAspects, ComponentAspect[] rejectedAspects, ComponentBox superBox) {
		super(neededAspects, rejectedAspects, superBox);
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void init() {
		this.locked = true;
	}

	final private void ensureNotLocked() {
		if (this.locked) {
			throw new BoxLockedException(this);
		}
	}

	public void addComponent(Object component, ComponentAspect[] aspects) {
		this.ensureNotLocked();

		super.addComponent(component, aspects);
	}

	@Override
	public void removeComponent(Object component) {
		this.ensureNotLocked();

		super.removeComponent(component);
	}

	public void addChildBox(ComponentBox componentBox) {
		this.ensureNotLocked();

		super.addChildBox(componentBox);
	}

	@Override
	public void removeChildBox(ComponentBox componentBox) {
		this.ensureNotLocked();

		super.removeChildBox(componentBox);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public boolean isLocked() {
		return locked;
	}
}
