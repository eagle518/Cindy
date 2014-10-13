/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.notifier
// NotifierListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 11, 2014 at 3:22:10 PM
////////

package co.mindie.cindy.notifier;

import me.corsin.javatools.misc.Pair;

import java.util.List;

public interface NotifierListener<T extends MobileNotification> {
	void onNotificationsSent(List<T> successfulNotification, List<Pair<T, Exception>> failedNotifications);

	void onNotificationsSent(List<T> successfulNotification, List<T> failedNotifications, Exception e);
}
