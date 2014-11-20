package co.mindie.cindy.queue;

import co.mindie.cindy.automapping.SearchScope;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.utils.Cancelable;
import co.mindie.cindy.utils.Flushable;
import org.apache.log4j.Logger;

import java.util.List;

public class WorkContext<DataType> {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(WorkContext.class);

	@Wired(searchScope = SearchScope.LOCAL) List<Flushable> flushables;
	@Wired(searchScope = SearchScope.LOCAL) List<Cancelable> cancelables;

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

	public void cancel() {
		this.cancelables.forEach(Cancelable::cancel);
	}

	public void flush() {
		this.flushables.forEach(Flushable::flush);
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
