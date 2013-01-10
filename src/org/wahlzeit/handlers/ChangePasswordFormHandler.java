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

import org.wahlzeit.model.UserRole;
import org.wahlzeit.model.UserLog;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.utils.StringUtil;
import org.wahlzeit.webparts.WebPart;



/**
 * 
 * @author dirkriehle
 *
 */
public class ChangePasswordFormHandler extends AbstractWebFormHandler {
	
	/**
	 *
	 */
	public ChangePasswordFormHandler() {
		initialize(PartUtil.CHANGE_PASSWORD_FORM_FILE, UserRole.class);
	}
	
	/**
	 * 
	 */
	protected void doMakeWebPart(UserSession ctx, WebPart part) {
		Map<String, Object> args = ctx.getSavedArgs();
		part.addStringFromArgs(args, UserSession.MESSAGE);

		UserRole user = ctx.getClient().getRole(UserRole.class);
		part.addStringFromArgsWithDefault(args, UserRole.PASSWORD, user.getPassword());
		part.addStringFromArgsWithDefault(args, UserRole.PASSWORD_AGAIN, user.getPassword());
	}

	/**
	 * 
	 */
	protected String doHandlePost(UserSession ctx, Map args) {
		String password = ctx.getAndSaveAsString(args, UserRole.PASSWORD);
		String passwordAgain = ctx.getAndSaveAsString(args, UserRole.PASSWORD_AGAIN);
		
		if (StringUtil.isNullOrEmptyString(password)) {
			ctx.setMessage(ctx.cfg().getFieldIsMissing());
			return PartUtil.CHANGE_PASSWORD_PAGE_NAME;
		} else if (!password.equals(passwordAgain)) {
			ctx.setMessage(ctx.cfg().getPasswordsDontMatch());
			return PartUtil.CHANGE_PASSWORD_PAGE_NAME;
		} else if (!StringUtil.isLegalPassword(password)) {
			ctx.setMessage(ctx.cfg().getInputIsInvalid());
			return PartUtil.SIGNUP_PAGE_NAME;
		}

		UserRole user = ctx.getClient().getRole(UserRole.class);
		user.setPassword(password);
		
		UserLog.logPerformedAction("ChangePassword");
		
		ctx.setTwoLineMessage(ctx.cfg().getPasswordChangeSucceeded(), ctx.cfg().getContinueWithShowUserHome());

		return PartUtil.SHOW_NOTE_PAGE_NAME;
	}
	
}
