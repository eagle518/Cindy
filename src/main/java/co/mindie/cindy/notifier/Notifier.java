/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.notifier
// Notifier.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 11, 2014 at 3:22:05 PM
////////

package co.mindie.cindy.notifier;

import me.corsin.javatools.batch.BatchProcessor;
import me.corsin.javatools.batch.BatchProcessorListener;
import me.corsin.javatools.misc.Action;
import me.corsin.javatools.task.TaskQueue;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.util.List;

public abstract class Notifier implements BatchProcessorListener<MobileNotification>, Closeable {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(Notifier.class);
	public static final int DEFAULT_MAX_BATCH_SIZE = 500;

	private NotifierListener listener;
	private boolean enabled;
	private final BatchProcessor<MobileNotification> batchProcessor;
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

	protected abstract void processNotifications(List<MobileNotification> notifications);

	@Override
	public void handleBatchedEntities(
			BatchProcessor<MobileNotification> batchProcessor,
			List<MobileNotification> batchedEntities) {
		if (this.enabled) {
			this.processNotifications(batchedEntities);
		} else {
			LOGGER.trace("Should be sending notifications=" + batchedEntities);
		}
	}

	public void sendNotification(MobileNotification notification) {
		this.batchProcessor.addBatchEntity(notification);
	}

	protected void notifySendFailed(MobileNotification notification, Exception e) {
		this.getListener(listener -> listener.onNotificationSendFailed(notification, e));
	}

	protected void notifySent(MobileNotification notification) {
		this.getListener(listener -> listener.onNotificationSent(notification));
	}

	protected void notifySendFailed(List<MobileNotification> notifications, List<MobileNotification> success, List<MobileNotification> failures, Exception e) {
		this.getListener(listener -> listener.onNotificationGroupSendFailed(notifications, success, failures, e));
	}

	private void getListener(Action<NotifierListener> callback) {
		final NotifierListener listener = this.listener;
		final TaskQueue listenerTaskQueue = this.listenerTaskQueue;

		if (listener != null) {
			if (listenerTaskQueue == null) {
				callback.run(listener);
			} else {
				listenerTaskQueue.executeAsync(() -> callback.run(listener));
			}
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public NotifierListener getListener() {
		return this.listener;
	}

	public void setListener(NotifierListener listener) {
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
