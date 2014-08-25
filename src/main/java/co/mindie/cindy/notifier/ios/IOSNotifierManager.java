/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.notifier.ios
// IOSNotifierManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 31, 2014 at 5:10:00 PM
////////

package co.mindie.cindy.notifier.ios;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import co.mindie.cindy.component.CindyComponent;
import co.mindie.cindy.exception.CindyException;
import co.mindie.cindy.notifier.NotifierListener;
import me.corsin.javatools.misc.Pair;
import me.corsin.javatools.task.TaskQueue;

public class IOSNotifierManager extends CindyComponent implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////

	private Map<Pair<String, Boolean>, IOSNotifier> notifiers;
	private NotifierListener listener;
	private TaskQueue listenerTaskQueue;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public IOSNotifierManager() {
		this.notifiers = new HashMap<>();
	}

	////////////////////////
	// METHODS
	////////////////

	public IOSNotifier addNotifier(String bundleId, boolean useProduction, String keyFileName, String keyPassword) throws IOException {
		IOSNotifier notifier = this.addNotifier(bundleId, useProduction);

		if (notifier != null) {
			notifier.loadKeyUsingResourceFile(keyFileName);
		}
		notifier.setKeyPassword(keyPassword);

		return notifier;
	}

	public IOSNotifier addNotifier(String bundleId, boolean useProduction) {
		Pair<String, Boolean> key = new Pair<>(bundleId, useProduction);
		IOSNotifier notifier = this.notifiers.get(key);

		if (notifier != null) {
			throw new CindyException("A notifier with the same bundleId and useProduction value has been already set");
		}

		notifier = new IOSNotifier();
		notifier.setUseProductionServer(useProduction);
		notifier.setListener(this.listener);
		notifier.setListenerTaskQueue(this.listenerTaskQueue);
		this.notifiers.put(key, notifier);

		return notifier;
	}

	public IOSNotifier getNotifier(String bundleId, boolean useProduction) {
		Pair<String, Boolean> key = new Pair<>(bundleId, useProduction);
		return this.notifiers.get(key);
	}

	public void doOnNotifiers(Consumer<IOSNotifier> action) {
		this.notifiers.values().forEach(action);
	}

	@Override
	public void close() {
		for (IOSNotifier notifier : this.getNotifiers()) {
			notifier.close();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Collection<IOSNotifier> getNotifiers() {
		return this.notifiers.values();
	}

	public NotifierListener getListener() {
		return this.listener;
	}

	public TaskQueue getListenerTaskQueue() {
		return this.listenerTaskQueue;
	}

	public void setListener(NotifierListener listener) {
		this.listener = listener;
	}

	public void setListenerTaskQueue(TaskQueue listenerTaskQueue) {
		this.listenerTaskQueue = listenerTaskQueue;
	}
}