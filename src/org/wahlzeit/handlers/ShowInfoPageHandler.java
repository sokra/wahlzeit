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

package org.wahlzeit.handlers;

import javax.inject.Inject;

import org.wahlzeit.model.AccessRights;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.webparts.WebPart;

import com.google.inject.Injector;


/**
 * 
 * @author dirkriehle
 *
 */
public class ShowInfoPageHandler extends AbstractWebPageHandler {
	
	/**
	 * 
	 */
	protected String infoTmplName = null;
	
	/**
	 *
	 */
	protected ShowInfoPageHandler() {
	}
	
	/**
	 * 
	 */
	protected void makeWebPageBody(UserSession ctx, WebPart page) {
		page.addWritable("info", createWebPart(ctx, infoTmplName));
	}
	
	
	/**
	 * 
	 */
	public static class Factory {
		
		@Inject
		protected Injector injector;
		
		public ShowInfoPageHandler create(AccessRights myRights, String myInfoTmplName) {
			ShowInfoPageHandler handler = injector.getInstance(ShowInfoPageHandler.class);
			handler.initialize(PartUtil.SHOW_INFO_PAGE_FILE, myRights);
			handler.infoTmplName = myInfoTmplName;
			return handler;
		}
	}

}
