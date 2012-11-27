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

package org.wahlzeit.main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.wahlzeit.model.LanguageConfigs;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.services.ContextProvider;
import org.wahlzeit.services.Language;
import org.wahlzeit.services.Session;
import org.wahlzeit.services.SysConfig;
import org.wahlzeit.services.SysLog;
import org.wahlzeit.utils.StringUtil;
import org.wahlzeit.webparts.WebPart;

/**
 * 
 * @author dirkriehle
 *
 */
public abstract class AbstractServlet extends HttpServlet {
	
	@Inject
	protected ContextProvider contextProvider;
	
	@Inject
	protected Main main;
	
	@Inject
	protected SysConfig sysConfig;
	
	@Inject
	protected SysLog sysLog;
	
	@Inject
	protected LanguageConfigs languageConfigs;
	
	@Inject
	protected UserSession.Factory userSessionFactory;
	
	/**
	 * 
	 */
	protected static int lastSessionId = 0; // system and agent are named differently
	private static final long serialVersionUID = 42L; // any one does; class never serialized
	
	/**
	 * 
	 */
	public static synchronized int getLastSessionId() {
		return lastSessionId;
	}
	
	/**
	 * 
	 */
	public static void setLastSessionId(int newSessionId) {
		lastSessionId = newSessionId;
	}
	
	/**
	 * 
	 */
	public static synchronized int getNextSessionId() {
		return ++lastSessionId;
	}
	
	/**
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserSession ctx = ensureWebContext(request);	
		contextProvider.set(ctx);
		
		if (main.isShuttingDown() || (ctx == null)) {
			displayNullPage(request, response);
		} else {
			myGet(request, response);
			ctx.dropDatabaseConnection();
		}

		contextProvider.drop();
	}
	
	/**
	 * 
	 */
	protected void myGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// do nothing
	}
		
	/**
	 * 
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserSession ctx = ensureWebContext(request);	
		contextProvider.set(ctx);
		
		if (main.isShuttingDown() || (ctx == null)) {
			displayNullPage(request, response);
		} else {
			myPost(request, response);
			ctx.dropDatabaseConnection();
		}

		contextProvider.drop();
	}
	
	/**
	 * 
	 */
	protected void myPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// do nothing
	}

	/**
	 * 
	 */
	protected UserSession ensureWebContext(HttpServletRequest request) {
		HttpSession httpSession = request.getSession();
		UserSession result = (UserSession) httpSession.getAttribute("context");
		if (result == null) {
			try {
				String ctxName = "ctx" + getNextSessionId();
				result = userSessionFactory.create(ctxName);
				sysLog.logCreatedObject("WebContext", ctxName);

				// yes, "Referer"; typo in original standard documentation
				String referrer = request.getHeader("Referer");
				sysLog.logInfo("request referrer: " + referrer);

				if (request.getLocale().getLanguage().equals("de")) {
					result.setConfiguration(languageConfigs.get(Language.GERMAN));
				}
			} catch (Exception ex) {
				sysLog.logThrowable(ex);
			}
			
			httpSession.setAttribute("context", result);
			httpSession.setMaxInactiveInterval(24 * 60 * 60); // time out after 24h
		}
		
		return result;
	}

	/**
	 * 
	 */
	protected void displayNullPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		out.print("The system is undergoing maintenance and will be back in a minute. Thank you for your patience!");
		out.close();

		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	/**
	 * 
	 */
	protected void redirectRequest(HttpServletResponse response, String link) throws IOException {
		response.setContentType("text/html");
		response.sendRedirect(link + ".html");
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	/**
	 * 
	 */
	protected void configureResponse(Session ctx, HttpServletResponse response, WebPart result) throws IOException {
		long processingTime = ctx.getProcessingTime();
		result.addString("processingTime", StringUtil.asStringInSeconds((processingTime == 0) ? 1 : processingTime));
		sysLog.logValue("proctime", String.valueOf(processingTime));
		
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		result.writeOn(out);
		out.close();

		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	/**
	 * 
	 */
	protected boolean isLocalHost(HttpServletRequest request) {
		String remoteHost = request.getRemoteHost();
		String localHost = null;
		try {
			localHost = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception ex) {
			// ignore
		}
		return remoteHost.equals(localHost) || remoteHost.equals("localhost");
	}
	
	/**
	 * 
	 */
	protected String getRequestArgsAsString(UserSession ctx, Map<String, ?> args) {
		StringBuffer result = new StringBuffer(96);
		for (Iterator<String> i = args.keySet().iterator(); i.hasNext(); ) {
			String key = i.next().toString();
			String value = ctx.getAsString(args, key);
			result.append(key + "=" + value);
			if (i.hasNext()) {
				 result.append("; ");
			}
		}
		return "[" + result.toString() + "]";
	}

}
