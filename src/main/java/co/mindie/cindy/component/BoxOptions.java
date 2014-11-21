package co.mindie.cindy.component;

/**
 * Created by simoncorsin on 20/11/14.
 */
public class BoxOptions {

	////////////////////////
	// VARIABLES
	////////////////

	final private ComponentAspect[] neededAspects;
	final private ComponentAspect[] rejectedAspects;
	final private boolean readOnly;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public BoxOptions(ComponentAspect[] neededAspects, ComponentAspect[] rejectedAspects, boolean readOnly) {
		this.neededAspects = neededAspects;
		this.rejectedAspects = rejectedAspects;
		this.readOnly = readOnly;
	}

	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public ComponentAspect[] getNeededAspects() {
		return neededAspects;
	}

	public ComponentAspect[] getRejectedAspects() {
		return rejectedAspects;
	}

	public boolean isReadOnly() {
		return readOnly;
	}
}
