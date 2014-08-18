/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.notifier.android
// AndroidNotificationRequest.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 11, 2014 at 4:14:43 PM
////////

package co.mindie.wsframework.notifier.android;

import java.util.Map;

public class AndroidNotificationRequest {

	////////////////////////
	// VARIABLES
	////////////////

	private String[] registrationIds;
	private String notificationKey;
	private String collapseKey;
	private Map<String, Object> data;
	private Boolean delayWhileIdle;
	private Integer timeToLive;
	private String restrictedPackageName;
	private Boolean dryRun;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public AndroidNotificationRequest() {

	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public String[] getRegistrationIds() {
		return this.registrationIds;
	}

	public String getNotificationKey() {
		return this.notificationKey;
	}

	public String getCollapseKey() {
		return this.collapseKey;
	}

	public Map<String, Object> getData() {
		return this.data;
	}

	public Boolean getDelayWhileIdle() {
		return this.delayWhileIdle;
	}

	public Integer getTimeToLive() {
		return this.timeToLive;
	}

	public String getRestrictedPackageName() {
		return this.restrictedPackageName;
	}

	public Boolean getDryRun() {
		return this.dryRun;
	}

	public void setRegistrationIds(String[] registrationIds) {
		this.registrationIds = registrationIds;
	}

	public void setNotificationKey(String notificationKey) {
		this.notificationKey = notificationKey;
	}

	public void setCollapseKey(String collapseKey) {
		this.collapseKey = collapseKey;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public void setDelayWhileIdle(Boolean delayWhileIdle) {
		this.delayWhileIdle = delayWhileIdle;
	}

	public void setTimeToLive(Integer timeToLive) {
		this.timeToLive = timeToLive;
	}

	public void setRestrictedPackageName(String restrictedPackageName) {
		this.restrictedPackageName = restrictedPackageName;
	}

	public void setDryRun(Boolean dryRun) {
		this.dryRun = dryRun;
	}

}
