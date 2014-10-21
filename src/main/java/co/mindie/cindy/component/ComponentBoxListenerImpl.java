package co.mindie.cindy.component;

/**
 * Created by simoncorsin on 20/10/14.
 */
public class ComponentBoxListenerImpl implements ComponentBoxListener {

	////////////////////////
	// VARIABLES
	////////////////

	private ComponentBox innerBox;
	private ComponentBox enclosingBox;

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public ComponentBox getInnerBox() {
		return innerBox;
	}

	public void setInnerBox(ComponentBox innerBox) {
		this.innerBox = innerBox;
	}

	public ComponentBox getEnclosingBox() {
		return enclosingBox;
	}

	public void setEnclosingBox(ComponentBox enclosingBox) {
		this.enclosingBox = enclosingBox;
	}
}
