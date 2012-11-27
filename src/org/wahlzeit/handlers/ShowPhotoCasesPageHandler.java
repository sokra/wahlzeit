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
import org.wahlzeit.model.PhotoCase;
import org.wahlzeit.model.PhotoCaseManager;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.utils.HtmlUtil;
import org.wahlzeit.webparts.WebPart;
import org.wahlzeit.webparts.Writable;
import org.wahlzeit.webparts.WritableList;



/**
 * 
 * @author dirkriehle
 *
 */
public class ShowPhotoCasesPageHandler extends AbstractWebPageHandler {
	
	@Inject
	protected PhotoCaseManager photoCaseManager;
	
	/**
	 *
	 */
	protected ShowPhotoCasesPageHandler() {
		initialize(PartUtil.SHOW_PHOTO_CASES_PAGE_FILE, AccessRights.MODERATOR);
	}

	/**
	 * 
	 */
	protected void makeWebPageBody(UserSession ctx, WebPart page) {
		Map<String, ?> args = ctx.getSavedArgs();
		page.addStringFromArgs(args, UserSession.MESSAGE);
		
		PhotoCaseManager pcm = photoCaseManager;
		PhotoCase[] flaggedCases = pcm.getOpenPhotoCasesByAscendingAge();
		if (flaggedCases.length != 0) {
			WritableList openCases = new WritableList();
			for (int i = 0; i < flaggedCases.length; i++) {
				openCases.append(makePhotoCaseForm(ctx, flaggedCases[i]));
			}
			page.addWritable("openCases", openCases);
		} else {
			page.addString("openCases", HtmlUtil.asPara(ctx.cfg().getNoFlaggedPhotoCases()));
		}
	}
	
	/**
	 * 
	 */
	protected Writable makePhotoCaseForm(UserSession ctx, PhotoCase photoCase) {
		ctx.setPhotoCase(photoCase);
		WebFormHandler handler = getFormHandler(PartUtil.EDIT_PHOTO_CASE_FORM_NAME);
		return handler.makeWebPart(ctx);
	}
	
}
