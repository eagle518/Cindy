/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.notifier.android
// Test.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 11, 2014 at 4:04:35 PM
////////

package co.mindie.cindy.notifier.android;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import co.mindie.cindy.notifier.MobileNotification;
import co.mindie.cindy.notifier.NotifierListener;

public class Test {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Test() {

	}

	////////////////////////
	// METHODS
	////////////////

	public static void main(String[] args) {
		AndroidNotifier notifier = new AndroidNotifier();
		notifier.setAuthorizationKey("AIzaSyDjH4KBu1AFcPWgfxk_75lvMxYxQDTD520");
		notifier.setListener(new NotifierListener() {

			@Override
			public void onNotificationSent(MobileNotification notification) {
				System.out.println("Well done ");
			}

			@Override
			public void onNotificationSendFailed(MobileNotification notification,
					Exception e) {
				e.printStackTrace();
			}

			@Override
			public void onNotificationGroupSendFailed(
					List<MobileNotification> allNotifications,
					List<MobileNotification> successfulNotifications,
					List<MobileNotification> failedNotification, Exception e) {
				e.printStackTrace();
			}
		});

		Map<String, Object> body = new HashMap<>();
		body.put("notification_id", new Random(new Date().getTime()).nextInt());
		body.put("notification_title", "You received a message!");
		body.put("notification_content", "Damned");
		System.out.println("Id: " + body.get("notification_id"));

		notifier.sendNotification("APA91bGeqGLs2NDpPeW7LVLTbZVi9l5_q26AzH_sHGVjXJC7xbFrzrmwBQjx5fysqk0DRihULrZ2Bp187Mg_Oiv9y_dbmQoCwm3poOmNSl5fR7jkP7zcnvv-BnFw6hLZsnRCkw3FpNjmUtJoinjLKPr55vvRUV6Gqg", body, null);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
