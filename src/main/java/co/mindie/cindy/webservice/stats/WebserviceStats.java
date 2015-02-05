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
	private AtomicInteger successCounter;
	private AtomicInteger failedCounter;
	private int currentRequestsPerSecond;
	private int maxRequestsPerSecond;
	private int successPerSecond;
	private int failsPerSecond;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public WebserviceStats() {
		super("Webservice Stats", new TimeSpan(1));

		this.counter = new AtomicInteger();
		this.successCounter = new AtomicInteger();
		this.failedCounter = new AtomicInteger();
	}


	////////////////////////
	// METHODS
	////////////////

	@Override
	public void run() {
		this.currentRequestsPerSecond = this.counter.getAndSet(0);
		this.successPerSecond = this.successCounter.getAndSet(0);
		this.failsPerSecond = this.failedCounter.getAndSet(0);

		if (this.currentRequestsPerSecond > this.maxRequestsPerSecond) {
			this.maxRequestsPerSecond = this.currentRequestsPerSecond;
		}
	}

	public void notifyRequestStarted() {
		this.counter.incrementAndGet();
	}

	public void notifyRequestEnded(boolean hasSucceed) {
		if (hasSucceed) {
			this.successCounter.incrementAndGet();
		} else {
			this.failedCounter.incrementAndGet();
		}
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

	public int getFailsPerSecond() {
		return failsPerSecond;
	}

	public int getSuccessPerSecond() {
		return successPerSecond;
	}
}
