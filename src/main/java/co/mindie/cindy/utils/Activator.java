package co.mindie.cindy.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simoncorsin on 21/11/14.
 */
public class Activator {

	////////////////////////
	// VARIABLES
	////////////////

	volatile private boolean activated;
	private List<Runnable> onActivatedListeners = new ArrayList<>();
	private List<Runnable> onDesactivatedListeners = new ArrayList<>();
	final private Object activatedNotifier = new Object();
	final private Object deactivatedNotifier = new Object();

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	public void activate() {
		synchronized (this) {
			if (!this.activated) {
				this.activated = true;

				synchronized (this.activatedNotifier) {
					this.activatedNotifier.notifyAll();
				}

				this.onActivatedListeners.forEach(Runnable::run);
			}
		}
	}

	public void deactivate() {
		synchronized (this) {
			if (this.activated) {
				this.activated = false;

				synchronized (this.deactivatedNotifier) {
					this.deactivatedNotifier.notifyAll();
				}

				this.onDesactivatedListeners.forEach(Runnable::run);
			}

		}
	}

	public void waitUntilActivated() {
		this.waitUntilActivated(0);
	}

	public boolean waitUntilActivated(long timeoutMs) {
		boolean hasTimeout = false;
		synchronized (this.activatedNotifier) {
			if (!this.activated) {
				try {
					if (timeoutMs == 0) {
						this.activatedNotifier.wait();
					} else {
						this.activatedNotifier.wait(timeoutMs);
					}
				} catch (InterruptedException ignored) {
				}
			}
			hasTimeout = !this.activated;
		}

		return !hasTimeout;
	}

	public void waitUntilDeactivated() {
		this.waitUntilDeactivated(0);
	}

	public boolean waitUntilDeactivated(long timeoutMs) {
		boolean hasTimeout = false;
		synchronized (this.deactivatedNotifier) {
			if (this.activated) {
				try {
					if (timeoutMs == 0) {
						this.deactivatedNotifier.wait();
					} else {
						this.deactivatedNotifier.wait(timeoutMs);
					}
				} catch (InterruptedException ignored) {
				}
			}
			hasTimeout = this.activated;
		}

		return !hasTimeout;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public void onActivated(Runnable handler) {
		synchronized (this) {
			this.onActivatedListeners.add(handler);

			if (this.activated) {
				handler.run();
			}
		}
	}

	public void onDeactivated(Runnable handler) {
		synchronized (this) {
			this.onDesactivatedListeners.add(handler);

			if (!this.activated) {
				handler.run();
			}
		}
	}

	public boolean isActivated() {
		return activated;
	}
}
