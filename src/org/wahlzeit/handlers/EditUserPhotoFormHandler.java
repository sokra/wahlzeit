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

import javax.inject.Inject;

import org.wahlzeit.model.AccessRights;
import org.wahlzeit.model.Photo;
import org.wahlzeit.model.PhotoManager;
import org.wahlzeit.model.PhotoStatus;
import org.wahlzeit.model.Tags;
import org.wahlzeit.model.UserLog;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.utils.HtmlUtil;
import org.wahlzeit.webparts.WebPart;

/**
 * 
 * @author dirkriehle
 *
 */
public class EditUserPhotoFormHandler extends AbstractWebFormHandler {
	
	@Inject
	protected UserLog userLog;
	
	@Inject
	protected PhotoManager photoManager;

	/**
	 *
	 */
	protected EditUserPhotoFormHandler() {
		initialize(PartUtil.EDIT_USER_PHOTO_FORM_FILE, AccessRights.USER);
	}
	
	/**
	 * 
	 */
	protected boolean isWellFormedGet(UserSession ctx, String link, Map<String, ?> args) {
		return hasSavedPhotoId(ctx);
	}

	/**
	 * 
	 */
	protected void doMakeWebPart(UserSession ctx, WebPart part) {
		Map<String, Object> args = ctx.getSavedArgs();
		part.addStringFromArgs(args, UserSession.MESSAGE);

		String id = ctx.getAsString(args, Photo.ID);
		Photo photo = photoManager.getPhoto(id);

		part.addString(Photo.ID, id);
		part.addString(Photo.THUMB, getPhotoThumb(ctx, photo));
		
		part.addString(Photo.PRAISE, photo.getPraiseAsString(ctx.cfg()));
		part.maskAndAddString(Photo.TAGS, photo.getTags().asString());
		
		part.addString(Photo.IS_INVISIBLE, HtmlUtil.asCheckboxCheck(photo.getStatus().isInvisible()));
		part.addString(Photo.STATUS, ctx.cfg().asValueString(photo.getStatus()));
		part.addString(Photo.UPLOADED_ON, ctx.cfg().asDateString(photo.getCreationTime()));	
	}
	
	/**
	 * 
	 */
	protected boolean isWellFormedPost(UserSession ctx, Map<String, ?> args) {
		String id = ctx.getAsString(args, Photo.ID);
		Photo photo = photoManager.getPhoto(id);
		return (photo != null) && ctx.isPhotoOwner(photo);
	}
	
	/**
	 * 
	 */
	protected String doHandlePost(UserSession ctx, Map<String, ?> args) {
		String id = ctx.getAndSaveAsString(args, Photo.ID);
		PhotoManager pm = photoManager;
		Photo photo = photoManager.getPhoto(id);

		String tags = ctx.getAndSaveAsString(args, Photo.TAGS);
		photo.setTags(new Tags(tags));

		String status = ctx.getAndSaveAsString(args, Photo.IS_INVISIBLE);
		boolean isInvisible = (status != null) && status.equals("on");
		PhotoStatus ps = photo.getStatus().asInvisible(isInvisible);
		photo.setStatus(ps);

		pm.savePhoto(photo);
		
		StringBuffer sb = userLog.createActionEntry("EditUserPhoto");
		userLog.addUpdatedObject(sb, "Photo", photo.getId().asString());
		userLog.log(sb);
		
		ctx.setTwoLineMessage(ctx.cfg().getPhotoUpdateSucceeded(), ctx.cfg().getContinueWithShowUserHome());

		return PartUtil.SHOW_NOTE_PAGE_NAME;
	}
	
}
