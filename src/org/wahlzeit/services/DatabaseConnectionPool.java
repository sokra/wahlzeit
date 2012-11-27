/*
 * Copyright (c) 2006-2009 by Dirk Riehle, http://dirkriehle.com
 *
 * This file is part of the Wahlzeit photo rating application.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package org.wahlzeit.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import com.google.inject.Injector;

/**
 * A database connection wraps an RDMBS connection object.
 * It pools and reuses existing connections; it caches common SQL statements.
 *
 * @author dirkriehle
 *
 */
public class DatabaseConnectionPool {
	
	@Inject
	protected SysConfig sysConfig;
	
	@Inject
	protected SysLog sysLog;
	
	@Inject
	protected Injector injector;
	
	/**
	 * 
	 */
	protected Set<DatabaseConnection> pool = new HashSet<DatabaseConnection>();
	
	/**
	 * 
	 */
	protected int dbcId = 0;
	
	/**
	 * 
	 */
	public DatabaseConnectionPool() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException ex) {
			sysLog.logThrowable(ex);
		}
	}
	
	/**
	 * 
	 */
	public synchronized DatabaseConnection getInstance() throws SQLException {
		DatabaseConnection result = null;
		if (pool.isEmpty()) {
			result = new DatabaseConnection("dbc" + dbcId++, openRdbmsConnection());
			injector.injectMembers(result);
			sysLog.logCreatedObject("DatabaseConnection", result.getName());
		} else {
			result = pool.iterator().next();
			pool.remove(result);
		}
		
		return result;
	}
	
	/**
	 * 
	 */
	public synchronized void dropInstance(DatabaseConnection dbc) {
		if (dbc != null) {
			pool.add(dbc);
		} else {
			sysLog.logError("returned null to database to pool");
		}
	}
	
	/**
	 * 
	 */
	public Connection openRdbmsConnection() throws SQLException {
		String dbConnection = sysConfig.getDbConnectionAsString();
		String dbUser = sysConfig.getDbUserAsString();
		String dbPassword = sysConfig.getDbPasswordAsString();
   		Connection result = DriverManager.getConnection(dbConnection, dbUser, dbPassword);
   		sysLog.logInfo("opening database connection: " + result.toString());
   		return result;
	}

}
