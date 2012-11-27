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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.servlet.MultiPartRequest;
import org.wahlzeit.handlers.PartUtil;
import org.wahlzeit.handlers.WebFormHandler;
import org.wahlzeit.handlers.WebPageHandler;
import org.wahlzeit.handlers.WebPartHandlerManager;
import org.wahlzeit.model.UserLog;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.webparts.WebPart;



/**
 * 
 * @author dirkriehle
 *
 */
public class MainServlet extends AbstractServlet {
	
	@Inject
	protected UserLog userLog;
	
	@Inject
	protected WebPartHandlerManager webPartHandlerManager;

	/**
	 * 
	 */
	private static final long serialVersionUID = 42L; // any one does; class never serialized

	/**
	 * 
	 */
	public void myGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();
		UserSession ctx = ensureWebContext(request);
		
		String link = request.getRequestURI();
		int linkStart = link.lastIndexOf("/") + 1;
		int linkEnd = link.indexOf(".html");
		if (linkEnd == -1) {
			linkEnd = link.length();
		}
		
		link = link.substring(linkStart, linkEnd);
		userLog.logValue("requested", link);

		WebPageHandler handler = webPartHandlerManager.getWebPageHandler(link);
		String newLink = PartUtil.DEFAULT_PAGE_NAME;
		if (handler != null) {
			Map<String, ?> args = getRequestArgs(request);
			sysLog.logInfo("GET arguments: " + getRequestArgsAsString(ctx, args));
			newLink = handler.handleGet(ctx, link, args);
		}

		if (newLink.equals(link)) { // no redirect necessary
			WebPart result = handler.makeWebPart(ctx);
			ctx.addProcessingTime(System.currentTimeMillis() - startTime);
			configureResponse(ctx, response, result);
			ctx.clearSavedArgs(); // saved args go from post to next get
			ctx.resetProcessingTime();
		} else {
			sysLog.logValue("redirect", newLink);
			redirectRequest(response, newLink);
			ctx.addProcessingTime(System.currentTimeMillis() - startTime);
		}
	}
	
	/**
	 * 
	 */
	public void myPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();
		UserSession ctx = ensureWebContext(request);
		
		String link = request.getRequestURI();
		int linkStart = link.lastIndexOf("/") + 1;
		int linkEnd = link.indexOf(".form");
		if (linkEnd != -1) {
			link = link.substring(linkStart, linkEnd);
		} else {
			link = PartUtil.NULL_FORM_NAME;
		}
		userLog.logValue("postedto", link);
			
		Map<String, ?> args = getRequestArgs(request);
		sysLog.logInfo("POST arguments: " + getRequestArgsAsString(ctx, args));
		
		WebFormHandler formHandler = webPartHandlerManager.getWebFormHandler(link);
		link = PartUtil.DEFAULT_PAGE_NAME;
		if (formHandler != null) {
			link = formHandler.handlePost(ctx, args);
		}

		redirectRequest(response, link);
		ctx.addProcessingTime(System.currentTimeMillis() - startTime);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, ?> getRequestArgs(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();
        if ((contentType != null) && contentType.startsWith("multipart/form-data")) {
        	MultiPartRequest multiPartRequest = new MultiPartRequest(request);
			return getRequestArgs(multiPartRequest);
		} else {
			return (Map<String, ?>) request.getParameterMap();
		}
	}

	/**
	 * 
	 */
	protected Map<String, ?> getRequestArgs(MultiPartRequest request) throws IOException {
		Map<String, String> result = new HashMap<String, String>();

		String[] keys = request.getPartNames();
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			String value = null;
			if (key.equals("fileName")) {
				InputStream in = request.getInputStream(key);
				String tempName = sysConfig.getTempDirAsString() + Thread.currentThread().getId();
				FileOutputStream out = new FileOutputStream(new File(tempName));
				int uploaded = 0;
				for (int avail = in.available(); (avail > 0) && (uploaded < 1000000); avail = in.available()) {
					byte[] buffer = new byte[avail];
					in.read(buffer, 0, avail);
					out.write(buffer);
					uploaded += avail;
				}
				out.close();
				value = tempName;
			} else {
				value = request.getString(key);
			}
			result.put(key, value);
		}
		
		return result;
	}

}
