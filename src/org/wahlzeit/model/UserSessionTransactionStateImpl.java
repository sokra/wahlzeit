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

package org.wahlzeit.model;

import java.util.HashMap;
import java.util.Map;

import org.wahlzeit.utils.HtmlUtil;

public class UserSessionTransactionStateImpl implements UserSessionTransactionState {
	/**
	 * Transaction state
	 */
	public Map<String, Object> savedArgs = new HashMap<String, Object>();
	
	/**
	 * 
	 */
	@Override
	public String getHeading() {
		return (String) savedArgs.get(HEADING);
	}
	
	/**
	 * 
	 */
	@Override
	public void setHeading(String myHeading) {
		savedArgs.put(HEADING, myHeading);
	}
	
	/**
	 * 
	 */
	@Override
	public String getMessage() {
		return (String) savedArgs.get(MESSAGE);
	}
	
	/**
	 * 
	 */
	@Override
	public void setMessage(String myMessage) {
		savedArgs.put(MESSAGE, HtmlUtil.asPara(myMessage));
	}
	
	/**
	 * 
	 */
	@Override
	public void setTwoLineMessage(String msg1, String msg2) {
		savedArgs.put(MESSAGE, HtmlUtil.asPara(msg1) + HtmlUtil.asPara(msg2));
	}
	
	/**
	 * 
	 */
	@Override
	public void setThreeLineMessage(String msg1, String msg2, String msg3) {
		savedArgs.put(MESSAGE, HtmlUtil.asPara(msg1) + HtmlUtil.asPara(msg2) + HtmlUtil.asPara(msg3));
	}
	
	/**
	 * 
	 */
	@Override
	public Photo getPhoto() {
		return (Photo) savedArgs.get(PHOTO);
	}
	
	/**
	 * 
	 */
	@Override
	public void setPhoto(Photo newPhoto) {
		savedArgs.put(PHOTO, newPhoto);
	}
		
	/**
	 * 
	 */
	@Override
	public Photo getPriorPhoto() {
		return (Photo) savedArgs.get(PRIOR_PHOTO);
	}
	
	/**
	 * 
	 */
	@Override
	public void setPriorPhoto(Photo oldPhoto) {
		savedArgs.put(PRIOR_PHOTO, oldPhoto);
	}
	
	/**
	 * 
	 */
	@Override
	public PhotoCase getPhotoCase() {
		return (PhotoCase) savedArgs.get(PHOTO_CASE);
	}
	
	/**
	 * 
	 */
	@Override
	public void setPhotoCase(PhotoCase photoCase) {
		savedArgs.put(PHOTO_CASE, photoCase);
	}

	/**
	 * 
	 */
	@Override
	public String getAsString(Map<String, ?> args, String key) {
		String result = null;
		
		Object value = args.get(key);
		if (value == null) {
			result = "";
		} else if (value instanceof String) {
			result = (String) value;
		} else if (value instanceof String[]) {
			String[] array = (String[]) value;
			result = array[0];
		} else {
			result = value.toString();
		}
		
		return result;
	}

	/**
	 * 
	 */
	@Override
	public String getAndSaveAsString(Map<String, ?> args, String key) {
		String result = getAsString(args, key);
		savedArgs.put(key, result);
		return result;
	}
	
	/**
	 * 
	 */
	@Override
	public Object getSavedArg(String key) {
		return savedArgs.get(key);
	}

	/**
	 * 
	 */
	@Override
	public void setSavedArg(String key, Object value) {
		savedArgs.put(key, value);
	}
	
	/**
	 * 
	 */
	@Override
	public Map<String, Object> getSavedArgs() {
		return savedArgs;
	}
	
	/**
	 * 
	 */
	@Override
	public void clearSavedArgs() {
		savedArgs.clear();
	}
}