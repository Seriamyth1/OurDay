package com.ourday.database;

import java.sql.SQLException;
import java.time.ZonedDateTime;

import com.ourday.model.CalendarReminder;
import com.ourday.notification.NotificationManager;
import com.ourday.service.ReminderService;
import com.ourday.service.ReminderScheduler;
import com.ourday.model.CalendarEvent;
import com.ourday.database.EventRepository;

public class Main {

    public static void main(String[] args) {

        ReminderScheduler scheduler = null;

        try {
        	DatabaseManager.initializeDatabase();

        	ReminderRepository reminderRepository = new ReminderRepository();
        	EventRepository eventRepository = new EventRepository();

        	ReminderService reminderService =
        	    new ReminderService(reminderRepository);

        	NotificationManager notificationManager =
        	    new NotificationManager();

        	scheduler = new ReminderScheduler(
        	    reminderService,
        	    notificationManager
        	);

        	// Display the existing event information
        	for (CalendarEvent event : eventRepository.loadEvents()) {

        	    if (event.getEventID() == 2) {
        	        System.out.println("Testing event: " + event.getTitle());
        	    }
        	}

        	// Create a reminder scheduled 10 seconds from now
        	CalendarReminder testReminder = new CalendarReminder(
        	    2,
        	    ZonedDateTime.now().plusSeconds(10)
        	);

        	CalendarReminder savedReminder =
        	    reminderRepository.saveReminder(testReminder);

        	System.out.println("Created Reminder ID: "
        	    + savedReminder.getReminderID());

        	System.out.println("Scheduled for: "
        	    + savedReminder.getReminderTime());

        	// Start automatic checking
        	scheduler.start();

        	System.out.println("Waiting for event notification...");

        	// Wait for the scheduler's second check
        	Thread.sleep(40000);

        	System.out.println("Test complete.");

        } catch (SQLException e) {
            System.err.println(
                "Database error: " + e.getMessage()
            );
            e.printStackTrace();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

        } finally {
            if (scheduler != null) {
                scheduler.stop();
            }
        }
    }
}