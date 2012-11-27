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
import org.wahlzeit.model.LanguageConfigs;
import org.wahlzeit.model.PhotoSize;
import org.wahlzeit.model.UserLog;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.services.Language;
import org.wahlzeit.webparts.WebPart;

/**
 * 
 * @author dirkriehle
 *
 */
public class SetOptionsFormHandler extends AbstractWebFormHandler {
	
	@Inject
	protected UserLog userLog;
	
	@Inject
	protected LanguageConfigs languageConfigs;
	
	/**
	 * 
	 */
	public static final String LANGUAGE = "language";
	public static final String PHOTO_SIZE = "photoSize";
	
	/**
	 *
	 */
	protected SetOptionsFormHandler() {
		initialize(PartUtil.SET_OPTIONS_FORM_FILE, AccessRights.GUEST);
	}
	
	/**
	 * 
	 */
	protected void doMakeWebPart(UserSession ctx, WebPart part) {
		Map<String, ?> args = ctx.getSavedArgs();
		part.addStringFromArgs(args, UserSession.MESSAGE);
		
//FIXME		part.addString(WebContext.MESSAGE, ctx.getMessage());
		
		part.addSelect(LANGUAGE, Language.class, (String) args.get(LANGUAGE));
		part.addSelect(PHOTO_SIZE, PhotoSize.class, (String) args.get(PHOTO_SIZE));
	}
	
	/**
	 * 
	 */
	protected String doHandlePost(UserSession ctx, Map<String, ?> args) {
		String language = ctx.getAndSaveAsString(args, LANGUAGE);
		Language langValue = Language.getFromString(language);
		ctx.setConfiguration(languageConfigs.get(langValue));

		String photoSize = ctx.getAndSaveAsString(args, PHOTO_SIZE);
		PhotoSize photoValue = PhotoSize.getFromString(photoSize);
		ctx.setPhotoSize(photoValue);
		
		StringBuffer sb = userLog.createActionEntry("SetOptions");
		userLog.addField(sb, "Language", language);
		userLog.addField(sb, "PhotoSize", photoSize);
		userLog.log(sb);
		
		String msg1 = ctx.cfg().getOptionsWereSet();
		String msg2 = ctx.cfg().getNoteMaximumPhotoSize();
		String msg3 = ctx.cfg().getContinueWithShowPhoto();
		ctx.setThreeLineMessage(msg1, msg2, msg3);

		return PartUtil.SHOW_NOTE_PAGE_NAME;
	}
	
}
