package co.mindie.cindy.core.tools;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A CounterActivator is an Activator that adds a counter
 * with bounds. Activation is done automatically
 * depending on the counter value.
 */
public class CounterActivator extends Activator {

	////////////////////////
	// VARIABLES
	////////////////

	private AtomicInteger counter;
	private int deactivateBound;
	private int activateBound;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CounterActivator() {
		this.counter = new AtomicInteger();
		this.activateBound = 1;
	}

	////////////////////////
	// METHODS
	////////////////

	public void increment() {
		if (this.counter.incrementAndGet() >= this.activateBound) {
			this.activate();
		}
	}

	public void decrement() {
		if (this.counter.decrementAndGet() <= this.deactivateBound) {
			this.deactivate();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public int getDeactivateBound() {
		return deactivateBound;
	}

	public void setDeactivateBound(int deactivateBound) {
		this.deactivateBound = deactivateBound;
	}

	public int getActivateBound() {
		return activateBound;
	}

	public void setActivateBound(int activateBound) {
		this.activateBound = activateBound;
	}

	public int getCounterValue() {
		return this.counter.get();
	}

	public void setCounterValue(int value) {
		this.counter.set(value);
		if (value >= this.activateBound) {
			this.activate();
		} else if (value <= this.deactivateBound) {
			this.deactivate();
		}
	}
}
