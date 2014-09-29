package co.mindie.cindy.queue;

import co.mindie.cindy.automapping.Wired;

/**
 * Created by simoncorsin on 29/09/14.
 */
public class WorkContext<DataType> {

	////////////////////////
	// VARIABLES
	////////////////

	@Wired private WorkQueue<DataType> queue;
	@Wired private WorkProcessor<DataType> workProcessor;

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	public void prepareForProcessing() {

	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public WorkQueue<DataType> getQueue() {
		return queue;
	}

	public WorkProcessor<DataType> getWorkProcessor() {
		return workProcessor;
	}
}
