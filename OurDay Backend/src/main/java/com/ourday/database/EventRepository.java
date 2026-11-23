package com.ourday.database;

import java.sql.*;
import com.ourday.model.CalendarEvent;
import java.time.*;
import java.util.*;

public class EventRepository {
	
	public CalendarEvent saveEvent(CalendarEvent event)	throws SQLException{
			String sql = """
					INSERT INTO events
					(title, description, start_time, end_time, time_zone, created_at)
					VALUES (?, ?, ?, ?, ?, ?)
					""";
		
		try (Connection connection = DatabaseManager.getConnection();
				//https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html API reference
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			statement.setString(1, event.getTitle());
			statement.setString(2, event.getDescription());
			statement.setString(3, event.getStartTime().toOffsetDateTime().toString());
			statement.setString(4, event.getEndTime().toOffsetDateTime().toString());
			statement.setString(5, event.getStartTime().getZone().getId());
			statement.setString(6, event.getCreatedAt().toString());
			
			statement.executeUpdate();
			
			//review the necessity of this statement since getEventID is not needed since auto-assigned from SQLite database incrementally
			try (ResultSet keys = statement.getGeneratedKeys()) {
				if(keys.next()) {
					long generatedID = keys.getLong(1);
					return new CalendarEvent(generatedID, event.getTitle(), event.getDescription(), event.getStartTime(), event.getEndTime(), event.getCreatedAt());}
				}
			throw new SQLException("Event saved, but no generated ID was returned.");
		}
	
	}
	
	public List<CalendarEvent> loadEvents() throws SQLException {
		List<CalendarEvent> events = new ArrayList<>();
		
		String sql = "SELECT * FROM events ORDER BY start_time";
		
		try(Connection connection = DatabaseManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet results = statement.executeQuery()){
			
			while(results.next()) {
				long eventID = results.getLong("event_id");
				String title = results.getString("title");
				String description = results.getString("description");
				ZoneId zone = ZoneId.of(results.getString("time_zone"));
				ZonedDateTime startTime = ZonedDateTime.parse(results.getString("start_time")).withZoneSameInstant(zone);
				ZonedDateTime endTime = ZonedDateTime.parse(results.getString("end_time")).withZoneSameInstant(zone);
				ZonedDateTime createdAt = ZonedDateTime.parse(results.getString("created_at")).withZoneSameInstant(zone);
				CalendarEvent event = new CalendarEvent(eventID, title, description,startTime, endTime, createdAt);
				events.add(event);
			}
		}
		return events;
	}
	
	public boolean updateEvent(CalendarEvent event) throws SQLException{
		String sql = """
				UPDATE events
				SET title = ?,
				description = ?,
				start_time=?,
				end_time=?,
				time_zone=?
				WHERE event_id=?
				""";
		
		try(Connection connection = DatabaseManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, event.getTitle());
			statement.setString(2, event.getDescription());
			statement.setString(3, event.getStartTime().toOffsetDateTime().toString());
			statement.setString(4, event.getEndTime().toOffsetDateTime().toString());
			statement.setString(5, event.getStartTime().getZone().getId());
			statement.setLong(6, event.getEventID());
			
			int rowsUpdated = statement.executeUpdate();
			
			return rowsUpdated>0;
		}
	}
	
	public boolean deleteEvent(long eventID) throws SQLException {
		String sql = """
				DELETE FROM events WHERE event_id = ?
				""";
		
		try(Connection connection = DatabaseManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setLong(1, eventID);
			int rowsDeleted = statement.executeUpdate();
			return rowsDeleted> 0;
		}
	}
}