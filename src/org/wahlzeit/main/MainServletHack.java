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
import java.util.Enumeration;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.inject.Injector;


/**
 * 
 * TODO Find a better solution for this... 
 *
 */
public class MainServletHack implements Servlet, ServletConfig {
	
	/**
	 * 
	 */
	private static Injector injector;

	/**
	 * 
	 */
	public static void setInjector(Injector injector) {
		assert MainServletHack.injector == null || MainServletHack.injector == injector: "This do not work with this hack.";
		MainServletHack.injector = injector;
	}

	
	protected final MainServlet mainServlet;
	
	public MainServletHack() {
		mainServlet = injector.getInstance(MainServlet.class);
	}

	@Override
	public void destroy() {
		mainServlet.destroy();
	}

	@Override
	public ServletConfig getServletConfig() {
		return mainServlet.getServletConfig();
	}

	@Override
	public String getServletInfo() {
		return mainServlet.getServletInfo();
	}

	@Override
	public void init(ServletConfig arg0) throws ServletException {
		mainServlet.init(arg0);
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		mainServlet.service(arg0, arg1);
	}

	@Override
	public String getInitParameter(String arg0) {
		return mainServlet.getInitParameter(arg0);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration getInitParameterNames() {
		return mainServlet.getInitParameterNames();
	}

	@Override
	public ServletContext getServletContext() {
		return mainServlet.getServletContext();
	}

	@Override
	public String getServletName() {
		return mainServlet.getServletName();
	}

}
