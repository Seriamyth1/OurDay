package com.ourday.service;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ourday.notification.*;

public class ReminderScheduler {
	
	private final ReminderService reminderService;
	private final NotificationManager notificationManager;
	
	private ScheduledExecutorService scheduler;
	
	public ReminderScheduler(ReminderService reminderService, NotificationManager notificationManager) {
		this.reminderService=reminderService;
		this.notificationManager=notificationManager;
	}
	
	public void start() {
		if(scheduler != null && !scheduler.isShutdown()) {
			return;
		}
		
		scheduler = Executors.newSingleThreadScheduledExecutor();
		
		scheduler.scheduleWithFixedDelay(() ->{
			
			try {
				reminderService.processDueReminders(notificationManager);
			}catch(SQLException e) {
				System.err.println("Error checking reminders: " + e.getMessage());
				e.printStackTrace();
			}catch(RuntimeException e) {
				System.err.println("Unexcepted reminder error: " + e.getMessage());
				e.printStackTrace();
			}
		}, 0, 30, TimeUnit.SECONDS);
		System.out.println("Reminder scheduler started.");
	}

	public void stop() {
		if(scheduler != null) {
			scheduler.shutdown();
			System.out.println("Reminder scheduler stopped.");
		}
	}
}
