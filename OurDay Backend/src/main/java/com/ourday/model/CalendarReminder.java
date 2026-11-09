package com.ourday.model;

import java.time.ZonedDateTime;

public class CalendarReminder {
	
	private long reminderID;
	private long eventID;
	private ZonedDateTime reminderTime;
	private boolean enabled;
	private ZonedDateTime deliveredAt;
	
	//used when creating the initial reminder
	public CalendarReminder(long eventID, ZonedDateTime reminderTime) {
		this.eventID=eventID;
		this.reminderTime=reminderTime;
		this.enabled=true;
		this.deliveredAt=null;
	}
	
	//used when loading a reminder from the database
	public CalendarReminder(long reminderID, long eventID, ZonedDateTime reminderTime, boolean enabled, ZonedDateTime deliveredAt) {
		this.reminderID=reminderID;
		this.eventID=eventID;
		this.reminderTime=reminderTime;
		this.enabled=enabled;
		this.deliveredAt=deliveredAt;
	}
	
	public long getReminderID() {
		return reminderID;
	}
	
	public long getEventID() {
		return eventID;
	}
	
	public ZonedDateTime getReminderTime() {
		return reminderTime;
	}
	
	public void setReminderTime(ZonedDateTime reminderTime) {
		this.reminderTime=reminderTime;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled=enabled;
	}
	
	public ZonedDateTime getDeliveredAt() {
		return deliveredAt;
	}
	
	public void setDeliveredAt(ZonedDateTime deliveredAt) {
		this.deliveredAt=deliveredAt;
	}

}
