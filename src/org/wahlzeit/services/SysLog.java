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

import java.sql.Statement;
import java.text.DateFormat;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Logging class for logging system-level messages.
 * System-level log entries are the result of system services and activities.
 * 
 * @author dirkriehle
 *
 */
public class SysLog extends Log {
	
	/**
	 * More stuff
	 */
	protected final boolean isInProductionFlag;
	
	@Inject
	public SysLog(ContextProvider contextProvider, DateFormat dateFormatter, @Named("production") boolean isInProductionFlag) {
		super(contextProvider, dateFormatter);
		this.isInProductionFlag = isInProductionFlag;
		initialize();
	}
	
	/**
	 * 
	 */
	public void initialize() {
		if (isInProductionFlag) {
			logInfo("set to production mode");
		} else {
			logInfo("set to development mode");
		}
	}
	
	/**
	 * 
	 */
	public StringBuffer createSysLogEntry() {
		return createLogEntry("sl");
	}

	/**
	 * 
	 */
	public void logInfo(String s) {
		logInfo("sl", s);
	}
	
	/**
	 * 
	 */
	public void logError(String s) {
		logError("sl", s);
	}
	
	/**
	 * 
	 */
	public void logValue(String type, String value) {
		logValue("sl", type, value);
	}
	
	/**
	 * 
	 */
	public void logValueWithInfo(String type, String value, String info) {
		logValueWithInfo("sl", type, value, info);
	}
	
	/**
	 * 
	 */
	public void logCreatedObject(String type, String object) {
		logCreatedObject("sl", type, object);
	}
	
	/**
	 * 
	 */
	public final void logQuery(Statement q) {
		StringBuffer sb = createSysLogEntry();
		addLogType(sb, "info");
		addQuery(sb, q);
		log(sb);
	}
	
	/**
	 * 
	 */
	public final void logQuery(String s) {
		StringBuffer sb = createSysLogEntry();
		addLogType(sb, "info");
		addField(sb, "query", s);
		log(sb);
	}
	
	/**
	 * 
	 */
	public final void logThrowable(Throwable t) {
		StringBuffer sb = createSysLogEntry();
		addLogType(sb, "exception");
		addThrowable(sb, t);
		addStacktrace(sb, t);
		log(sb);
	}

}
