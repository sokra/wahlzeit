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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * A database connection wraps an RDMBS connection object.
 * It pools and reuses existing connections; it caches common SQL statements.
 *
 * @author dirkriehle
 *
 */
public class DatabaseConnection {
	
	@Inject
	protected SysConfig sysConfig;
	
	@Inject
	protected SysLog sysLog;
	
	/**
	 * 
	 */
	protected String name = null;
	
	/**
	 * 
	 */
	protected Connection rdbmsConnection = null;
	
	/**
	 * Map contains prepared statements retrieved by query string
	 */
	protected Map<String, PreparedStatement> readingStatements = new HashMap<String, PreparedStatement>();
	protected Map<String, PreparedStatement> updatingStatements = new HashMap<String, PreparedStatement>();

	/**
	 * 
	 */
	protected DatabaseConnection(String dbcName, Connection connection) throws SQLException {
		name = dbcName;
		rdbmsConnection = connection;
	}
	
	/**
	 * 
	 */
	protected void finalize() {
		try {
			closeConnection(rdbmsConnection);
		} catch (Throwable t) {
			sysLog.logThrowable(t);
		}
	}
	
	/**
	 * 
	 */
	public String getName() {
		return name;
	}
	
    /**
     * 
     */
    public Connection getRdbmsConnection() throws SQLException {
    	return rdbmsConnection;
    }
    
	/**
	 * 
	 */
	protected PreparedStatement getReadingStatement(String stmt) throws SQLException {
		PreparedStatement result = readingStatements.get(stmt);
		if (result == null) {
			result = getRdbmsConnection().prepareStatement(stmt);
			sysLog.logCreatedObject("PreparedStatement", result.toString());
	   		readingStatements.put(stmt, result);
		}
		
		return result;
	}
	
	/**
	 * 
	 */
	protected PreparedStatement getUpdatingStatement(String stmt) throws SQLException {
		PreparedStatement result = updatingStatements.get(stmt);
		if (result == null) {
			result = getRdbmsConnection().prepareStatement(stmt, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			sysLog.logCreatedObject("UpdatingStatement", result.toString());
	   		updatingStatements.put(stmt, result);
		}
		
		return result;
	}
	
	/**
	 * 
	 */
	public void closeConnection(Connection cn) throws SQLException {
		sysLog.logInfo("closing database connection: " + cn.toString());
		cn.close();
	}

}
