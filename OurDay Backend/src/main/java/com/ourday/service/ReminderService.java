package com.ourday.service;

import java.sql.*;
import java.time.*;
import java.util.*;

import com.ourday.notification.NotificationManager;
import com.ourday.database.ReminderRepository;
import com.ourday.model.CalendarReminder;
import com.ourday.database.EventRepository;
import com.ourday.model.CalendarEvent;

public class ReminderService {
	
	private ReminderRepository reminderRepository;
	
	public ReminderService(ReminderRepository reminderRepository) {
		this.reminderRepository=reminderRepository;
	}
	
	public List<CalendarReminder> getDueReminders() throws SQLException{
		List<CalendarReminder> dueReminders = new ArrayList<>();
		
		ZonedDateTime now = ZonedDateTime.now();
		
		for(CalendarReminder reminder : reminderRepository.loadReminders()) {
			//Reminder is on, hasn't been delivered, and is due
			if(reminder.isEnabled() && reminder.getDeliveredAt() ==null && !reminder.getReminderTime().isAfter(now)) {
				dueReminders.add(reminder);
			}
		}
		return dueReminders;
	}
	
	public boolean markAsDelivered(CalendarReminder reminder) throws SQLException{
		reminder.setDeliveredAt(ZonedDateTime.now());
		return reminderRepository.updateReminder(reminder);
	}
	
	public void processDueReminders(NotificationManager notificationManager) throws SQLException{
		List<CalendarReminder> dueReminders = getDueReminders();
		
		for(CalendarReminder reminder : dueReminders) {
			String title = "OurDay Reminder";
			
			String message = "You have a scheduled reminder for Event ID: " + reminder.getEventID();
			
			EventRepository eventRepository= new EventRepository();
			
			for(CalendarEvent event : eventRepository.loadEvents()) {
				if(event.getEventID() == reminder.getEventID()) {
					message = event.getTitle() + " - Your event is coming up!";
					break;
				}
			}
			
			boolean submitted = notificationManager.showNotification(title, message);
			if(submitted) {
				boolean marked = markAsDelivered(reminder);
				System.out.println("Reminder ID " + reminder.getReminderID() + " notification submitted. Recorded: " + marked);
			}
			else {
				System.out.println("Unable to submit notification for Reminder ID: " + reminder.getReminderID());
			}
		}
	}

}
