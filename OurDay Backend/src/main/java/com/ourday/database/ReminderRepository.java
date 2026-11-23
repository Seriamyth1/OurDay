package com.ourday.database;

import java.sql.*;
import com.ourday.model.CalendarReminder;
import java.time.*;
import java.util.*;

public class ReminderRepository {

	public CalendarReminder saveReminder(CalendarReminder reminder) throws SQLException {
		String sql = """
				INSERT INTO reminders (event_id, reminder_time, is_enabled, delivered_at) VALUES (?,?,?,?)
				""";
		
		try(Connection connection = DatabaseManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			statement.setLong(1, reminder.getEventID());
			statement.setString(2, reminder.getReminderTime().toString());
			//IsEnabled stored as int since SQLite does not have a native boolean class
			statement.setInt(3, reminder.isEnabled() ? 1 : 0);
			//getDelivered set to null until delivery and then timestamped when delivery happens
			if(reminder.getDeliveredAt() == null) {
				statement.setNull(4, java.sql.Types.VARCHAR);
			}
			else {
				statement.setString(4, reminder.getDeliveredAt().toString());
			}
			statement.executeUpdate();
			
			try(ResultSet keys = statement.getGeneratedKeys()){
				if(keys.next()) {
					long generatedID = keys.getLong(1);
					
					return new CalendarReminder(generatedID, reminder.getEventID(), reminder.getReminderTime(), reminder.isEnabled(), reminder.getDeliveredAt());
				}
			}
		}
		throw new SQLException ("Reminder saved, but no generated ID was returned.");
				
	}
	
	public List<CalendarReminder> loadReminders() throws SQLException{
		List<CalendarReminder> reminders = new ArrayList<>();
		
		String sql="""
				SELECT * FROM reminders ORDER BY reminder_time
				""";
		
		try(Connection connection = DatabaseManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet results = statement.executeQuery()){
					while(results.next()) {
						long reminderID = results.getLong("reminder_id");
						long eventID = results.getLong("event_id");
						ZonedDateTime reminderTime = ZonedDateTime.parse(results.getString("reminder_time"));
						boolean enabled = results.getInt("is_enabled") == 1;
						String deliveredAtValue = results.getString("delivered_at");
						ZonedDateTime deliveredAt = null;
						if(deliveredAtValue != null) {
							deliveredAt = ZonedDateTime.parse(deliveredAtValue);
						}
						
						CalendarReminder reminder = new CalendarReminder(reminderID, eventID, reminderTime, enabled, deliveredAt);
						reminders.add(reminder);
					}
				}
		return reminders;
	}
	
	public boolean updateReminder(CalendarReminder reminder) throws SQLException{
		String sql = """
				UPDATE reminders SET reminder_time = ?,
				is_enabled = ?,
				delivered_at=?
				WHERE reminder_id = ?
				""";
		
		try(Connection connection = DatabaseManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, reminder.getReminderTime().toString());
			statement.setInt(2, reminder.isEnabled() ? 1 : 0);
			if(reminder.getDeliveredAt() == null) {
				statement.setNull(3, java.sql.Types.VARCHAR);
			}
			else {
				statement.setString(3, reminder.getDeliveredAt().toString());
			}
			statement.setLong(4, reminder.getReminderID());
			int rowsUpdated = statement.executeUpdate();
			
			return rowsUpdated >0;
					
		}
	}
	
	public boolean deleteReminder(long reminderID) throws SQLException{
		String sql = """
				DELETE FROM reminders WHERE reminder_id = ?
				""";
		try(Connection connection = DatabaseManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setLong(1, reminderID);
			int rowsDeleted = statement.executeUpdate();
			return rowsDeleted >0;
		}
	}
	
}
