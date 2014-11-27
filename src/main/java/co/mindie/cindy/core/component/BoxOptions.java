package co.mindie.cindy.core.component;

/**
 * Created by simoncorsin on 20/11/14.
 */
public class BoxOptions {

	////////////////////////
	// VARIABLES
	////////////////

	final private Aspect[] neededAspects;
	final private Aspect[] rejectedAspects;
	final private boolean readOnly;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public BoxOptions(Aspect[] neededAspects, Aspect[] rejectedAspects, boolean readOnly) {
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

	public Aspect[] getNeededAspects() {
		return neededAspects;
	}

	public Aspect[] getRejectedAspects() {
		return rejectedAspects;
	}

	public boolean isReadOnly() {
		return readOnly;
	}
}
