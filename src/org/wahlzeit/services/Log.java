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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Statement;
import java.text.DateFormat;

import javax.inject.Inject;

/**
 * Simple logging class; should be replaced with log4j or the like.
 * 
 * @author dirkriehle
 *
 */
public class Log {
	
	/**
	 * 
	 */
	protected final DateFormat dateFormatter;
	
	protected final ContextProvider contextProvider;
	
	@Inject
	public Log(ContextProvider contextProvider, DateFormat dateFormatter) {
		this.contextProvider = contextProvider;
		this.dateFormatter = dateFormatter;
	}

	/**
	 * 
	 */
	public void logInfo(String l, String s) {
		StringBuffer sb = createLogEntry(l);
		addLogType(sb, "info");
		addField(sb, "info", s);
		log(sb);
	}
	
	/**
	 * 
	 */
	public void logError(String l, String s) {
		StringBuffer sb = createLogEntry(l);
		addLogType(sb, "error");
		addField(sb, "error", s);
		log(sb);
	}
	
	/**
	 * 
	 */
	public void logValue(String level, String type, String value) {
		StringBuffer sb = createLogEntry(level);
		addLogType(sb, "info");
		addField(sb, type, value);
		log(sb);
	}
	
	/**
	 * 
	 */
	public void logValueWithInfo(String level, String type, String value, String info) {
		StringBuffer sb = createLogEntry(level);
		addLogType(sb, "info");
		addField(sb, type, value);
		addField(sb, "info", info);
		log(sb);
	}
	
	/**
	 * 
	 */
	public void logCreatedObject(String level, String type, String object) {
		StringBuffer sb = createLogEntry(level);
		addLogType(sb, "info");
		addField(sb, "created", type);
		addField(sb, "object", object);
		log(sb);
	}

	/**
	 * 
	 */
	protected final StringBuffer createLogEntry(String level) {
		StringBuffer sb = new StringBuffer(256);
		String date = null;
		synchronized (dateFormatter) {
			date = dateFormatter.format(System.currentTimeMillis());
		}
		sb.append(date);
		addField(sb, "level", level);
		addContext(sb);
		return sb;
	}
	
	/**
	 * 
	 */
	public final void addField(StringBuffer sb, String type, String field) {
		sb.append(", " + type + "=" + field);
	}
	
	/**
	 * 
	 */
	public final void addContext(StringBuffer sb) {
		Session ctx = contextProvider.get();

		String id = (ctx != null) ? ctx.getName() : "noctx";
		addField(sb, "context", id);

		String dbc = (ctx != null) && ctx.hasDatabaseConnection() ? ctx.getDatabaseConnection().getName() : "nodbc";
		addField(sb, "dbconnection", dbc);
		
		String threadId = String.valueOf(Thread.currentThread().getId());
		addField(sb, "threadid", threadId);
		
		String cn = (ctx != null) ? ctx.getClientName() : "nocn";
		addField(sb, "client", cn);
	}
	
	/**
	 * 
	 */
	public final void addLogType(StringBuffer sb, String logType) {
		addField(sb, "logtype", logType);
	}
	
	/**
	 *
	 */
	public final void addThrowable(StringBuffer sb, Throwable t) {
		addField(sb, "throwable", t.toString());
	}
	
	/**
	 * 
	 */
	public final void addStacktrace(StringBuffer sb, Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		addField(sb, "stacktrace", sw.toString());
	}

	/**
	 * 
	 */
	public final void addQuery(StringBuffer sb, Statement q) {
		addField(sb, "query", q.toString());
	}

	/**
	 * 
	 */
	public final void log(StringBuffer sb) {
		System.out.println(sb);
	}
	
}
