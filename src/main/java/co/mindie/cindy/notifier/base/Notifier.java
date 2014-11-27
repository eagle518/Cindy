/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.notifier
// Notifier.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 11, 2014 at 3:22:05 PM
////////

package co.mindie.cindy.notifier.base;

import me.corsin.javatools.batch.BatchProcessor;
import me.corsin.javatools.batch.BatchProcessorListener;
import me.corsin.javatools.misc.Action;
import me.corsin.javatools.misc.Pair;
import me.corsin.javatools.task.TaskQueue;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.Flushable;
import java.util.List;

public abstract class Notifier<T extends MobileNotification> implements BatchProcessorListener<T>, Closeable, Flushable {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(Notifier.class);
	public static final int DEFAULT_MAX_BATCH_SIZE = 500;

	private NotifierListener<T> listener;
	private boolean enabled;
	private final BatchProcessor<T> batchProcessor;
	private TaskQueue listenerTaskQueue;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Notifier() {
		this.enabled = true;
		this.batchProcessor = new BatchProcessor<>();
		this.batchProcessor.setMaxBatchSize(DEFAULT_MAX_BATCH_SIZE);
		this.batchProcessor.setListener(this);
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void close() {
		this.batchProcessor.close();
	}

	protected abstract void processNotifications(List<T> notifications);

	@Override
	public void handleBatchedEntities(
			BatchProcessor<T> batchProcessor,
			List<T> batchedEntities) {
		if (this.enabled) {
			this.processNotifications(batchedEntities);
		} else {
			LOGGER.trace("Should be sending notifications=" + batchedEntities);
		}
	}

	public void sendNotification(T notification) {
		this.batchProcessor.addBatchEntity(notification);
	}

	protected void notifySent(List<T> successfulNotifications, List<Pair<T, Exception>> failedNotifications) {
		this.getListener(listener -> listener.onNotificationsSent(successfulNotifications, failedNotifications));
	}

	protected void notifySent(List<T> successfulNotifications, List<T> failedNotifications, Exception e) {
		this.getListener(listener -> listener.onNotificationsSent(successfulNotifications, failedNotifications, e));
	}

	private void getListener(Action<NotifierListener<T>> callback) {
		final NotifierListener<T> listener = this.getListener();
		final TaskQueue listenerTaskQueue = this.getListenerTaskQueue();

		if (listener != null) {
			if (listenerTaskQueue == null) {
				callback.run(listener);
			} else {
				listenerTaskQueue.executeAsync(() -> callback.run(listener));
			}
		}
	}

	public void flush() {
		this.batchProcessor.flush();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public NotifierListener<T> getListener() {
		return this.listener;
	}

	public void setListener(NotifierListener<T> listener) {
		this.listener = listener;
	}

	public void setMaxBatchSize(int maxBatchSize) {
		this.batchProcessor.setMaxBatchSize(maxBatchSize);
	}

	public int getMaxBatchSize() {
		return this.batchProcessor.getMaxBatchSize();
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public TaskQueue getListenerTaskQueue() {
		return this.listenerTaskQueue;
	}

	public void setListenerTaskQueue(TaskQueue listenerTaskQueue) {
		this.listenerTaskQueue = listenerTaskQueue;
	}

}
