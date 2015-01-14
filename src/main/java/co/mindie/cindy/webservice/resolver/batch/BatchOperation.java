package co.mindie.cindy.webservice.resolver.batch;

/**
 * Created by simoncorsin on 14/01/15.
 */
public class BatchOperation<INPUT, OUTPUT> {

	////////////////////////
	// VARIABLES
	////////////////

	private INPUT input;
	private OUTPUT output;

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public INPUT getInput() {
		return input;
	}

	public void setInput(INPUT input) {
		this.input = input;
	}

	public OUTPUT getOutput() {
		return output;
	}

	public void setOutput(OUTPUT output) {
		this.output = output;
	}
}
