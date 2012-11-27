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

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.wahlzeit.main.Main;
import org.wahlzeit.model.AccessRights;
import org.wahlzeit.model.Photo;
import org.wahlzeit.model.PhotoManager;
import org.wahlzeit.model.Saveable;
import org.wahlzeit.model.User;
import org.wahlzeit.model.UserManager;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.utils.StringUtil;
import org.wahlzeit.webparts.WebPart;
import org.wahlzeit.webparts.Writable;

/**
 * 
 * @author dirkriehle
 *
 */
public class ShowAdminPageHandler extends AbstractWebPageHandler implements WebFormHandler {
	
	@Inject
	protected PhotoManager photoManager;
	
	@Inject
	protected UserManager userManager;
	
	@Inject
	protected Set<Saveable> saveables;
	
	@Inject
	protected Main main;
	
	/**
	 * 
	 */
	protected ShowAdminPageHandler() {
		initialize(PartUtil.SHOW_ADMIN_PAGE_FILE, AccessRights.ADMINISTRATOR);
	}

	/**
	 * 
	 */
	protected void makeWebPageBody(UserSession ctx, WebPart page) {
		Map<String, ?> args = ctx.getSavedArgs();
		page.addStringFromArgs(args, UserSession.MESSAGE);
		
		Object userId = ctx.getSavedArg("userId");
		if(!StringUtil.isNullOrEmptyString(userId)) {
			page.addStringFromArgs(args, "userId");
			page.addWritable("object", makeAdminUserProfile(ctx));
		}

		Object photoId = ctx.getSavedArg("photoId");
		if(!StringUtil.isNullOrEmptyString(photoId)) {
			page.addStringFromArgs(args, "photoId");
			page.addWritable("object", makeAdminUserPhoto(ctx));
		}
	}
	
	/**
	 * 
	 */
	protected Writable makeAdminUserProfile(UserSession ctx) {
		WebFormHandler handler = getFormHandler(PartUtil.NULL_FORM_NAME);

		String userId = ctx.getSavedArg("userId").toString();
		User user = userManager.getUserByName(userId);
		if (user != null) {
			handler = getFormHandler(PartUtil.ADMIN_USER_PROFILE_FORM_NAME);
		}
		
		return handler.makeWebPart(ctx);
	}

	/**
	 * 
	 */
	protected Writable makeAdminUserPhoto(UserSession ctx) {
		WebFormHandler handler = getFormHandler(PartUtil.NULL_FORM_NAME);

		String photoId = ctx.getSavedArg("photoId").toString();
		Photo photo = photoManager.getPhoto(photoId);
		if (photo != null) {
			handler = getFormHandler(PartUtil.ADMIN_USER_PHOTO_FORM_NAME);
		}
		
		return handler.makeWebPart(ctx);
	}

	/**
	 * 
	 */
	public String handlePost(UserSession ctx, Map<String, ?> args) {
		if (!hasAccessRights(ctx, args)) {
			sysLog.logInfo("insufficient rights for POST from: " + ctx.getEmailAddressAsString());
			return getIllegalAccessErrorPage(ctx);
		}
				
		String result = PartUtil.SHOW_ADMIN_PAGE_NAME;
		
		if (ctx.isFormType(args, "adminUser")) {
			result = performAdminUserProfileRequest(ctx, args);
		} else if (ctx.isFormType(args, "adminPhoto")) {
			result = performAdminUserPhotoRequest(ctx, args);
		} else if (ctx.isFormType(args, "saveAll")) {
			result = performSaveAllRequest(ctx);
		} else if (ctx.isFormType(args, "shutdown")) {
			result = performShutdownRequest(ctx);
		}

		return result;
	}
	
	/**
	 * 
	 */
	protected String performAdminUserProfileRequest(UserSession ctx, Map<String, ?> args) {
		String userId = ctx.getAndSaveAsString(args, "userId");
		User user = userManager.getUserByName(userId);
		if (user == null) {
			ctx.setMessage(ctx.cfg().getUserNameIsUnknown());
		}
		
		return PartUtil.SHOW_ADMIN_PAGE_NAME;
	}

	/**
	 * 
	 */
	protected String performAdminUserPhotoRequest(UserSession ctx, Map<String, ?> args) {
		String photoId = ctx.getAndSaveAsString(args, "photoId");
		Photo photo = photoManager.getPhoto(photoId);
		if (photo == null) {
			ctx.setMessage(ctx.cfg().getPhotoIsUnknown());
		}
		
		return PartUtil.SHOW_ADMIN_PAGE_NAME;
	}

	/**
	 * 
	 */
	protected String performShutdownRequest(UserSession ctx) {
		sysLog.logInfo("shutting down");
		
		try {
			main.requestStop();
		} catch (Exception ex) {
			sysLog.logThrowable(ex);
		}
		
		ctx.setMessage("Shutting down...");
		return PartUtil.SHOW_NOTE_PAGE_NAME;
	}
	
	/**
	 * 
	 */
	protected String performSaveAllRequest(UserSession ctx) {
		sysLog.logInfo("saving objects");

		for(Saveable saveable: saveables)
			saveable.save();
		
		ctx.setMessage("Saved objects...");
		return PartUtil.SHOW_NOTE_PAGE_NAME;
	}	

}