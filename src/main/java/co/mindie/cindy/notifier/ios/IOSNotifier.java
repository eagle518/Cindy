/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.notifier
// IOSNotifier.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 31, 2014 at 4:20:56 PM
////////

package co.mindie.cindy.notifier.ios;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import co.mindie.cindy.notifier.MobileNotification;
import co.mindie.cindy.notifier.Notifier;
import javapns.Push;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.notification.Payload;
import javapns.notification.PayloadPerDevice;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.PushedNotifications;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;

public class IOSNotifier extends Notifier {

	////////////////////////
	// VARIABLES
	////////////////

	public static final int DEFAULT_MAX_BATCH_SIZE = 500;

	private byte[] key;
	private String keyPassword;
	private boolean useProductionServer;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public IOSNotifier() {
	}

	////////////////////////
	// PUBLIC API
	////////////////

	public void loadKeyUsingResourceFile(String fileName) throws IOException {
		InputStream stream = IOSNotifier.class.getClassLoader().getResourceAsStream(fileName);
		byte[] bytes = IOUtils.toByteArray(stream);
		this.key = bytes;
	}

	public void sendCombinedNotification(String iosToken, String message, String sound, Map<String, Object> body, int badge, Object userInfo) {
		this.sendNotification(IOSNotificationType.COMBINED, iosToken, message, sound, body, badge, userInfo);
	}

	public void sendAlertNotification(String iosToken, String message, String sound, Map<String, Object> body, Object userInfo) {
		this.sendNotification(IOSNotificationType.ALERT, iosToken, message, sound, body, 0, userInfo);
	}

	public void sendBadgeNotification(String iosToken, int badge, Object userInfo) {
		this.sendNotification(IOSNotificationType.BADGE, iosToken, null, null, null, badge, userInfo);
	}

	public void sendNotification(IOSNotificationType type, String iosToken, String message, String sound, Map<String, Object> body, int badge, Object userInfo) {
		IOSNotification notification = new IOSNotification();

		notification.setRecipientId(iosToken);
		notification.setType(type);
		notification.setMessage(message);
		notification.setBadge(badge);
		notification.setSound(sound);
		notification.setBody(body);
		notification.setUserInfo(userInfo);

		this.sendNotification(notification);
	}

	@Override
	public void sendNotification(MobileNotification notification) {
		super.sendNotification(notification);
	}

	////////////////////////
	// PRIVATE API
	////////////////

	@Override
	protected void processNotifications(List<MobileNotification> notifications) {
		List<PayloadPerDevice> payloadPerDevices = new ArrayList<>();

		for (int i = 0; i < notifications.size(); i++) {
			final IOSNotification notification = (IOSNotification)notifications.get(i);

			try {
				payloadPerDevices.add(this.generatePayloadPerDevice(notification));
			} catch (Exception e) {
				notifications.remove(i);
				i--;

				this.notifySendFailed(notification, e);
			}
		}

		this.process(notifications, payloadPerDevices);
	}

	private PayloadPerDevice generatePayloadPerDevice(IOSNotification notification) throws JSONException, InvalidDeviceTokenFormatException {
		Payload payload = null;
		switch (notification.getType()) {
			case COMBINED:
				payload = PushNotificationPayload.combined(notification.getMessage(), notification.getBadge(), notification.getSound());
				break;
			case BADGE:
				payload = PushNotificationPayload.badge(notification.getBadge());
				break;
			case ALERT:
				payload = PushNotificationPayload.alert(notification.getMessage());
				break;
		}

		Map<String, Object> body = notification.getBody();
		if (body != null) {
			for (Entry<String, Object> entry : body.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();

				if (value instanceof String) {
					payload.addCustomDictionary(key, (String)value);
				} else if (value instanceof Integer) {
					payload.addCustomDictionary(key, value);
				} else if (value instanceof List<?>) {
					payload.addCustomDictionary(key, (List<?>)value);
				} else {
					payload.addCustomDictionary(key, value);
				}
			}
		}

		PayloadPerDevice payloadPerDevice = new PayloadPerDevice(payload, notification.getRecipientId());

		return payloadPerDevice;
	}

	private void process(List<MobileNotification> notifications, List<PayloadPerDevice> payloadPerDevices) {
		List<MobileNotification> success = new ArrayList<>();
		List<MobileNotification> failures = new ArrayList<>();

		PushedNotifications ret;
		try {
			ret = Push.payloads(this.key, this.keyPassword, this.useProductionServer, 4, payloadPerDevices);

			for (int i = 0; i < ret.size(); i++) {
				PushedNotification not = ret.get(i);
				MobileNotification notification = notifications.get(i);
				if (not.isSuccessful()) {
					this.notifySent(notification);
					success.add(notification);
				} else {
					this.notifySendFailed(notification, not.getException());
					failures.add(notification);
				}
			}
		} catch (Exception e) {
			this.notifySendFailed(notifications, success, failures, e);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public byte[] getKey() {
		return this.key;
	}

	public boolean isUseProductionServer() {
		return this.useProductionServer;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

	public void setUseProductionServer(boolean useProductionServer) {
		this.useProductionServer = useProductionServer;
	}

	public String getKeyPassword() {
		return this.keyPassword;
	}

	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}

}
