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

	void onNotificationSent(MobileNotification notification);

	void onNotificationSendFailed(MobileNotification notification, Exception e);

	void onNotificationGroupSendFailed(List<MobileNotification> allNotifications, List<MobileNotification> successfulNotifications,
			List<MobileNotification> failedNotification, Exception e);

}
