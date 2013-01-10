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

import java.util.*;

import org.wahlzeit.agents.Agent;
import org.wahlzeit.agents.AgentManager;
import org.wahlzeit.agents.NotifyAboutPraiseAgent;
import org.wahlzeit.model.Photo;
import org.wahlzeit.model.PhotoManager;
import org.wahlzeit.model.UserLog;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.utils.StringUtil;
import org.wahlzeit.webparts.WebPart;



/**
 * 
 * @author dirkriehle
 *
 */
public class PraisePhotoFormHandler extends AbstractWebFormHandler {
	
	/**
	 * 
	 */
	public PraisePhotoFormHandler() {
		initialize(PartUtil.PRAISE_PHOTO_FORM_FILE, null);
	}
	
	/**
	 * 
	 */
	protected void doMakeWebPart(UserSession ctx, WebPart part) {
		Photo photo = ctx.getPhoto();
		if (photo != null) {
			String photoId = photo.getId().asString();
			part.addString(Photo.ID, photoId);
		}
	}
	
	/**
	 * 
	 */
	protected boolean isWellFormedPost(UserSession ctx, Map args) {
		String photoId = ctx.getAsString(args, Photo.ID);
		Photo photo = PhotoManager.getPhoto(photoId);
		return photo != null;
	}
	
	/**
	 * 
	 */
	protected String doHandlePost(UserSession ctx, Map args) {
		String photoId = ctx.getAsString(args, Photo.ID);
		Photo photo = PhotoManager.getPhoto(photoId);
		String praise = ctx.getAsString(args, Photo.PRAISE);

		boolean wasPraised = false;
		if (!StringUtil.isNullOrEmptyString(praise)) {
			if (!ctx.hasPraisedPhoto(photo)) {
				int value = Integer.parseInt(praise);
				photo.addToPraise(value);
				ctx.addPraisedPhoto(photo);
				wasPraised = true;
				if (photo.getOwnerNotifyAboutPraise()) {
					Agent agent = AgentManager.getInstance().getAgent(NotifyAboutPraiseAgent.NAME);
					NotifyAboutPraiseAgent notify = (NotifyAboutPraiseAgent) agent; 
					notify.addForNotify(photo);
				}
			}
		}
		
		ctx.setPriorPhoto(photo);

		UserLog.logPerformedAction(wasPraised ? "PraisePhoto" : "SkipPhoto");
		
		return PartUtil.SHOW_PHOTO_PAGE_NAME;
	}
	
}
