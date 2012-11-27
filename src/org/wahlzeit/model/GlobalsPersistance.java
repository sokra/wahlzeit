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

package org.wahlzeit.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.inject.Inject;

import org.wahlzeit.main.AbstractServlet;
import org.wahlzeit.services.ContextProvider;
import org.wahlzeit.services.DatabaseConnection;
import org.wahlzeit.services.SysLog;
import org.wahlzeit.utils.Lifecycle;

public class GlobalsPersistance implements Lifecycle, Saveable {
	
	@Inject
	protected SysLog sysLog;
	
	@Inject
	protected ContextProvider contextProvider;
	
	/**
	 * 
	 */
	protected void loadGlobals() throws SQLException {
		DatabaseConnection dbc = contextProvider.get().getDatabaseConnection();
		Connection conn = dbc.getRdbmsConnection();

		String query = "SELECT * FROM globals";
		sysLog.logQuery(query);

		Statement stmt = conn.createStatement();
		ResultSet result = stmt.executeQuery(query);
		if (result.next()) {
			int lastUserId = result.getInt("last_user_id");
			User.setLastUserId(lastUserId);
			sysLog.logInfo("loaded global variable lastUserId: " + lastUserId);
			int lastPhotoId = result.getInt("last_photo_id");
			PhotoId.setValue(lastPhotoId);
			sysLog.logInfo("loaded global variable lastPhotoId: " + lastPhotoId);
			int lastCaseId = result.getInt("last_case_id");
			Case.setLastCaseId(lastCaseId);
			sysLog.logInfo("loaded global variable lastCaseId: " + lastCaseId);
			int lastSessionId = result.getInt("last_session_id");
			AbstractServlet.setLastSessionId(lastSessionId);		
			sysLog.logInfo("loaded global variable lastSessionId: " + lastSessionId);
		} else {
			sysLog.logError("Could not load globals!");
		}
		
		stmt.close();
	}

	/**
	 *
	 */
	protected synchronized void saveGlobals() throws SQLException {
		DatabaseConnection dbc = contextProvider.get().getDatabaseConnection();
		Connection conn = dbc.getRdbmsConnection();

		String query = "SELECT * FROM globals";
		sysLog.logQuery(query);

		Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
		ResultSet rset = stmt.executeQuery(query);
		if (rset.next()) {
			int lastUserId = User.getLastUserId();
			rset.updateInt("last_user_id", lastUserId);
			sysLog.logInfo("saved global variable lastUserId: " + lastUserId);
			int lastPhotoId = PhotoId.getValue();
			rset.updateInt("last_photo_id", lastPhotoId);
			sysLog.logInfo("saved global variable lastPhotoId: " + lastPhotoId);
			int lastCaseId = Case.getLastCaseId();
			rset.updateInt("last_case_id", lastCaseId);
			sysLog.logInfo("saved global variable lastCaseId: " + lastCaseId);
			int lastSessionId = AbstractServlet.getLastSessionId();
			rset.updateInt("last_session_id", lastSessionId);
			sysLog.logInfo("saved global variable lastSessionId: " + lastSessionId);
			rset.updateRow();
		} else {
			sysLog.logError("Could not save globals!");
		}
		
		stmt.close();
	}

	@Override
	public void startUp() throws Exception {
		loadGlobals();
	}

	@Override
	public void shutDown() {
	}
	
	/**
	 * 
	 */
	@Override
	public void save() {
		try {
			saveGlobals();
		} catch(SQLException t) {
			sysLog.logThrowable(t);
		}
	}
	
}
