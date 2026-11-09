package com.ourday.model;

import java.time.ZonedDateTime;

public class CalendarEvent {
	
	private long eventID;
	private String title;
	private String description;
	private ZonedDateTime startTime;
	private ZonedDateTime endTime;
	private ZonedDateTime createdAt;
	
	public CalendarEvent (String title, String description, ZonedDateTime startTime, ZonedDateTime endTime) {
		this.title = title;
		this.description=description;
		this.startTime = startTime;
		this.endTime=endTime;
		this.createdAt=ZonedDateTime.now();
	}
	//second constructor used for events already created and assigned an eventID
	public CalendarEvent(long eventID, String title, String description, ZonedDateTime startTime, ZonedDateTime endTime, ZonedDateTime createdAt) {
		this.eventID=eventID;
		this.title=title;
		this.description=description;
		this.startTime=startTime;
		this.endTime=endTime;
		this.createdAt=createdAt;
	}
	
	public long getEventID() {
		return eventID;
	}
	
	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title=title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description=description;
	}
	
	public ZonedDateTime getStartTime() {
		return startTime;
	}
	
	public void setStartTime(ZonedDateTime startTime) {
		this.startTime=startTime;
	}
	
	public ZonedDateTime getEndTime() {
		return endTime;
	}
	
	public void setEndTime(ZonedDateTime endTime) {
		this.endTime=endTime;
	}
	
}
