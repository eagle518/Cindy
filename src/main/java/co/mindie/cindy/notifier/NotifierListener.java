/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.notifier
// NotifierListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 11, 2014 at 3:22:10 PM
////////

package co.mindie.cindy.notifier;

import java.util.List;

public interface NotifierListener {

	<T extends MobileNotification> void onNotificationSent(T notification);

	<T extends MobileNotification> void onNotificationSendFailed(T notification, Exception e);

	<T extends MobileNotification> void onNotificationGroupSendFailed(List<T> allNotifications, List<T> successfulNotifications,
	                                                                  List<T> failedNotification, Exception e);

}
