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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.inject.Inject;

import org.mortbay.util.IO;
import org.wahlzeit.model.AccessRights;
import org.wahlzeit.model.Photo;
import org.wahlzeit.model.PhotoManager;
import org.wahlzeit.model.Tags;
import org.wahlzeit.model.User;
import org.wahlzeit.model.UserLog;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.utils.StringUtil;
import org.wahlzeit.webparts.WebPart;

/**
 * 
 * @author dirkriehle
 *
 */
public class UploadPhotoFormHandler extends AbstractWebFormHandler {
	
	@Inject
	protected UserLog userLog;
	
	@Inject
	protected PhotoManager photoManager;
	
	/**
	 *
	 */
	protected UploadPhotoFormHandler() {
		initialize(PartUtil.UPLOAD_PHOTO_FORM_FILE, AccessRights.USER);
	}
	
	/**
	 * 
	 */
	protected void doMakeWebPart(UserSession ctx, WebPart part) {
		Map<String, Object> args = ctx.getSavedArgs();
		part.addStringFromArgs(args, UserSession.MESSAGE);

		part.maskAndAddStringFromArgs(args, Photo.TAGS);
	}
	
	/**
	 * 
	 */
	protected String doHandlePost(UserSession ctx, Map<String, ?> args) {
		String tags = ctx.getAndSaveAsString(args, Photo.TAGS);

		if (!StringUtil.isLegalTagsString(tags)) {
			ctx.setMessage(ctx.cfg().getInputIsInvalid());
			return PartUtil.UPLOAD_PHOTO_PAGE_NAME;
		}

		try {
			String sourceFileName = ctx.getAsString(args, "fileName");
			File file = new File(sourceFileName);
			Photo photo = photoManager.createPhoto(file);

			String targetFileName = sysConfig.getBackupDirAsString() + photo.getId().asString();
			createBackup(sourceFileName, targetFileName);
		
			User user = (User) ctx.getClient();
			user.addPhoto(photo); 
			
			photo.setTags(new Tags(tags));

			photoManager.savePhoto(photo);

			StringBuffer sb = userLog.createActionEntry("UploadPhoto");
			userLog.addCreatedObject(sb, "Photo", photo.getId().asString());
			userLog.log(sb);
			
			ctx.setTwoLineMessage(ctx.cfg().getPhotoUploadSucceeded(), ctx.cfg().getKeepGoing());
		} catch (Exception ex) {
			sysLog.logThrowable(ex);
			ctx.setMessage(ctx.cfg().getPhotoUploadFailed());
		}
		
		return PartUtil.UPLOAD_PHOTO_PAGE_NAME;
	}
	
	/**
	 * 
	 */
	protected void createBackup(String sourceName, String targetName) {
		try {
			File sourceFile = new File(sourceName);
			InputStream inputStream = new FileInputStream(sourceFile);
			File targetFile = new File(targetName);
			OutputStream outputStream = new FileOutputStream(targetFile);
			IO.copy(inputStream, outputStream);
		} catch (Exception ex) {
			sysLog.logInfo("could not create backup file of photo");
			sysLog.logThrowable(ex);			
		}
	}
}
