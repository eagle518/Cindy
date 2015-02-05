package co.mindie.cindy.webservice.stats;

import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.webservice.annotation.Singleton;
import co.mindie.cindy.worker.AbstractWorker;
import me.corsin.javatools.timer.TimeSpan;

import java.util.concurrent.atomic.AtomicInteger;

@Singleton
@Load
public class WebserviceStats extends AbstractWorker {

	////////////////////////
	// VARIABLES
	////////////////

	private AtomicInteger counter;
	private int currentRequestsPerSecond;
	private int maxRequestsPerSecond;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public WebserviceStats() {
		super("Webservice Stats", new TimeSpan(1));

		this.counter = new AtomicInteger();
	}


	////////////////////////
	// METHODS
	////////////////

	@Override
	public void run() {
		this.currentRequestsPerSecond = this.counter.getAndSet(0);

		if (this.currentRequestsPerSecond > this.maxRequestsPerSecond) {
			this.maxRequestsPerSecond = this.currentRequestsPerSecond;
		}
	}

	public void notifyRequestStarted() {
		this.counter.incrementAndGet();
	}

	public void notifyRequestEnded() {

	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public int getCurrentRequestsPerSecond() {
		return currentRequestsPerSecond;
	}

	public int getMaxRequestsPerSecond() {
		return maxRequestsPerSecond;
	}
}
