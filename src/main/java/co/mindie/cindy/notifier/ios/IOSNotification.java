/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.notifier
// IOSNotification.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 31, 2014 at 4:40:52 PM
////////

package co.mindie.cindy.notifier.ios;

import co.mindie.cindy.notifier.base.MobileNotification;

public class IOSNotification extends MobileNotification {

	////////////////////////
	// VARIABLES
	////////////////

	private IOSNotificationType type;
	private String message;
	private int badge;
	private String sound;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public IOSNotification() {

	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public String getMessage() {
		return this.message;
	}

	public int getBadge() {
		return this.badge;
	}

	public String getSound() {
		return this.sound;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setBadge(int badge) {
		this.badge = badge;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	public IOSNotificationType getType() {
		return this.type;
	}

	public void setType(IOSNotificationType type) {
		this.type = type;
	}
}
