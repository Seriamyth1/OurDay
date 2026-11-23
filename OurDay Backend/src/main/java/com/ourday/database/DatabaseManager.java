package com.ourday.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
	
	//Location of database. JDBC: Java Database Connectivity 
	private static final String DB_URL = "jdbc:sqlite:ourday.db";
	
	//Establishes connection with connection object, throws exception otherwise.
	public static Connection getConnection()
		throws SQLException {
		//Opens connection to SQLite
		Connection connection = DriverManager.getConnection(DB_URL);
		
		try (Statement statement = connection.createStatement()){
			//Foreign keys are not associated with SQLite by default, so this is added so it can be utilized later on.
			//https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html#execute-- API reference
			statement.execute("PRAGMA foreign_keys = ON");
		} catch (SQLException e) {
			connection.close();
			throw e;
		}
		return connection;
	}
	
	public static void initializeDatabase() throws SQLException{
		String createEventsTable = """
				CREATE TABLE IF NOT EXISTS events (
				event_id INTEGER PRIMARY KEY AUTOINCREMENT,
				title TEXT NOT NULL,
				description TEXT,
				start_time TIMESTAMP,
				end_time TIMESTAMP,
				time_zone TEXT NOT NULL,
				created_at TEXT NOT NULL			
				)
				""";
		
		String createRemindersTable = """
				CREATE TABLE IF NOT EXISTS reminders (
				reminder_id INTEGER PRIMARY KEY AUTOINCREMENT,
				event_id INTEGER NOT NULL,
				reminder_time TIMESTAMP,
				is_enabled INTEGER NOT NULL DEFAULT 1,
				delivered_at TIMESTAMP,
				FOREIGN KEY (event_id)
					REFERENCES events(event_id)
					ON DELETE CASCADE		
				)
				""";
		
		try(Connection connection = getConnection();
				Statement statement = connection.createStatement()){
			statement.execute(createEventsTable);
			statement.execute(createRemindersTable);
			
			System.out.println("OurDay database tables initialized!");
		}
	}

}
