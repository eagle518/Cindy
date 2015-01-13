package co.mindie.cindy.core.component.metadata;

/**
 * Created by simoncorsin on 13/01/15.
 */
public class LoadInstruction {

	////////////////////////
	// VARIABLES
	////////////////

	private int creationPriority;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public LoadInstruction(int creationPriority) {
		this.creationPriority = creationPriority;
	}

	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public int getCreationPriority() {
		return creationPriority;
	}
}
