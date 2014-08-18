/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.notifier
// Notification.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 11, 2014 at 3:23:01 PM
////////

package co.mindie.cindy.notifier;

import java.util.Map;

public class MobileNotification {

	////////////////////////
	// VARIABLES
	////////////////

	private String recipientId;
	private Map<String, Object> body;
	private Object userInfo;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public MobileNotification() {

	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public String getRecipientId() {
		return this.recipientId;
	}

	public Map<String, Object> getBody() {
		return this.body;
	}

	public Object getUserInfo() {
		return this.userInfo;
	}

	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}

	public void setBody(Map<String, Object> body) {
		this.body = body;
	}

	public void setUserInfo(Object userInfo) {
		this.userInfo = userInfo;
	}
}
