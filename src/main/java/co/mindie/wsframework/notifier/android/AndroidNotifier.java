/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.notifier.android
// AndroidNotifier.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 11, 2014 at 3:13:05 PM
////////

package co.mindie.wsframework.notifier.android;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.corsin.javatools.http.CommunicatorResponse;
import me.corsin.javatools.http.HttpMethod;
import me.corsin.javatools.http.ServerRequest;
import me.corsin.javatools.http.StringResponseTransformer;
import co.mindie.wsframework.notifier.MobileNotification;
import co.mindie.wsframework.notifier.Notifier;
import co.mindie.wsframework.responseserializer.JsonResponseWriter;

public class AndroidNotifier extends Notifier {

	////////////////////////
	// VARIABLES
	////////////////

	private String authorizationKey;
	private JsonResponseWriter jsonWriter;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public AndroidNotifier() {
		this.jsonWriter = new JsonResponseWriter();
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	protected void processNotifications(List<MobileNotification> notifications) {
		if (this.authorizationKey != null) {
			for (MobileNotification notification : notifications) {
				try {
					this.process(notification);
					this.notifySent(notification);
				} catch (Exception e) {
					this.notifySendFailed(notification, e);
				}
			}
		} else {
			this.notifySendFailed(notifications, new ArrayList<>(), notifications, new Exception("No authorization key set in the AndroidNotifier"));
		}
	}

	public void sendNotification(String recipientId, Map<String, Object> body, Object userInfo) {
		MobileNotification notification = new MobileNotification();
		notification.setBody(body);
		notification.setRecipientId(recipientId);
		notification.setUserInfo(userInfo);

		this.sendNotification(notification);
	}

	private void process(MobileNotification mobileNotification) throws IOException {
		ServerRequest request = new ServerRequest();
		request.setURL("https://android.googleapis.com/gcm/send");
		request.setHttpMethod(HttpMethod.POST);

		AndroidNotificationRequest notificationRequest = new AndroidNotificationRequest();
		notificationRequest.setData(mobileNotification.getBody());
		notificationRequest.setRegistrationIds(new String[] { mobileNotification.getRecipientId() });

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		this.jsonWriter.writeResponse(notificationRequest, outputStream);


		byte[] outputArray = outputStream.toByteArray();

		System.out.println(new String(outputArray, "UTF-8"));
		request.setBody(new ByteArrayInputStream(outputArray), "application/json", outputArray.length);
		request.getHeaders().put("Authorization", "key=" + this.authorizationKey);

		request.setResponseTransformer(new StringResponseTransformer());

		CommunicatorResponse<String, String> response = request.getResponse(String.class, String.class);

		if (response.getFailureObjectResponse() != null) {
			throw new IOException(response.getFailureObjectResponse());
		}
		System.out.println(response.getSuccessObjectResponse());
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public String getAuthorizationKey() {
		return this.authorizationKey;
	}

	public void setAuthorizationKey(String authorizationKey) {
		this.authorizationKey = authorizationKey;
	}

}
