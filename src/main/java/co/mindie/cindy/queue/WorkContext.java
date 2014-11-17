package co.mindie.cindy.queue;

/**
 * Created by simoncorsin on 29/09/14.
 */
public class WorkContext<DataType> {

	////////////////////////
	// VARIABLES
	////////////////

	private WorkQueue<DataType> queue;
	private WorkProcessor<DataType> workProcessor;

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

	public void setQueue(WorkQueue<DataType> queue) {
		this.queue = queue;
	}

	public void setWorkProcessor(WorkProcessor<DataType> workProcessor) {
		this.workProcessor = workProcessor;
	}
}
