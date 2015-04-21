/*	  Update schedule database for pcmrbot
 *    Copyright (C) 2015  James Wolff
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.jwolff52.updateschedule.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

	private static Connection conn;

	private static final String URL = "jdbc:mysql://localhost:3306/pcmrbot?";

	public static final String DATABASE = "pcmrbot";

	static final Logger logger = Logger.getLogger(Database.class + "");

	/**
	 * Creates a connection to the database.
	 * 
	 * @return - true if connection is successful
	 */
	public static boolean initDBConnection(String pass) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			logger.log(
					Level.SEVERE,
					"Unable to find Driver in classpath!"
							,e);
		}
		try {
			conn = DriverManager.getConnection(String.format("%suser=bot&password=%s", URL, pass));
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	/**
	 * Creates the tables for the provided channel
	 * 
	 * @param channelNoHash - the channel we are connecting to.
	 * @return - true if it has to create the tables
	 */
	public static boolean getTables() {
		Statement stmt;
		Statement stmt1;
		try {
			stmt = conn.createStatement();
			stmt.closeOnCompletion();
			stmt.executeQuery(String.format("SELECT * FROM %s.pcmrSchedule", DATABASE));
			return false;
		} catch (SQLException e) {
			try {
				stmt1 = conn.createStatement();
				stmt1.closeOnCompletion();
				stmt1.executeUpdate(String.format("CREATE TABLE %s.pcmrSchedule(day INTEGER, streamer varchar(50), game varchar(255), startTime varchar(10), endTime varchar(10))", DATABASE));
			} catch (SQLException ex) {
				logger.log(Level.SEVERE, String.format("Unable to create table %sMods!",DATABASE), ex);
			}
			return true;
		}
	}

	/**
	 * Sends an update to the database (eg. INSERT, DELETE, etc.)
	 * 
	 * @param sqlCommand
	 * @return - true if it successfully executes the update
	 */
	private static boolean executeUpdate(String sqlCommand) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.closeOnCompletion();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, String.format("Unable to create connection for SQLCommand: %s", sqlCommand), e);
			return false;
		}
		try {
			stmt.executeUpdate(sqlCommand);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, String.format("Unable to execute statment: %s", sqlCommand), e);
			return false;
		}
		return true;
	}
	
	public static void resetTable() {
		executeUpdate(String.format("DROP TABLE %s.pcmrSchedule", DATABASE));
		executeUpdate(String.format("CREATE TABLE %s.pcmrSchedule(day varchar(3), streamer varchar(50), game varchar(255), startTime varchar(10), endTime varchar(10))", DATABASE));
	}
	
	public static void addStream(String day, String info) {
		int d = 1;
		switch(day.toLowerCase()) {
		case "sun": 
			break;
		case "mon": 
			d = 2;
			break;
		case "tue": 
			d = 3;
			break;
		case "wed": 
			d = 4;
			break;
		case "thu": 
			d = 5;
			break;
		case "fri": 
			d = 6;
			break;
		case "sat": 
			d = 7;
		}
		System.out.println(info);
		String streamer = null;
		try {
			streamer = info.substring(0, info.indexOf(':'));
		} catch (StringIndexOutOfBoundsException e) {
			executeUpdate(String.format("INSERT INTO %s.pcmrSchedule VALUES(\'%d\',\'%s\',\'%s\',\'%s\',\'%s\')", DATABASE, d, info, null, null, null));
			return;
		}
		String game = info.substring(info.indexOf(":") + 2, info.lastIndexOf("at") - 1);
		String start = info.substring(info.lastIndexOf("at") + 3, info.lastIndexOf('-'));
		String end = info.substring(info.lastIndexOf('-') + 1);
		executeUpdate(String.format("INSERT INTO %s.pcmrSchedule VALUES(\'%d\',\'%s\',\'%s\',\'%s\',\'%s\')", DATABASE, d, streamer, game, start, end));
	}
}
