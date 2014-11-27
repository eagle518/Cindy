/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.notifier.android
// AndroidNotification.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 11, 2014 at 3:17:09 PM
////////

package co.mindie.cindy.notifier.android;

import co.mindie.cindy.notifier.base.MobileNotification;

public class AndroidNotification extends MobileNotification {

	////////////////////////
	// VARIABLES
	////////////////

	private String registrationId;
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


	public Object getUserInfo() {
		return this.userInfo;
	}

	public void setUserInfo(Object userInfo) {
		this.userInfo = userInfo;
	}

	public String getRegistrationId() {
		return this.registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

}
