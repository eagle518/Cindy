/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.notifier.android
// AndroidNotification.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 11, 2014 at 3:17:09 PM
////////

package co.mindie.cindy.notifier.android;

import java.util.Map;

public class AndroidNotification {

	////////////////////////
	// VARIABLES
	////////////////

	private String registrationId;
	private Map<String, String> body;
	private Object userInfo;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public AndroidNotification() {

	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public String getRegistrationId() {
		return this.registrationId;
	}

	public Map<String, String> getBody() {
		return this.body;
	}

	public Object getUserInfo() {
		return this.userInfo;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public void setBody(Map<String, String> body) {
		this.body = body;
	}

	public void setUserInfo(Object userInfo) {
		this.userInfo = userInfo;
	}
}
