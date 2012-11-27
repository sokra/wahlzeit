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

import java.util.Map;

public interface UserSessionTransactionState {

	/**
	 * 
	 */
	public static final String PHOTO = "photo";
	public static final String PRIOR_PHOTO = "priorPhoto";
	public static final String PHOTO_CASE = "photoCase";
	public static final String MESSAGE = "message";
	public static final String HEADING = "heading";
	public static final String USER = "user";

	/**
	 * 
	 */
	String getHeading();

	/**
	 * 
	 */
	void setHeading(String myHeading);

	/**
	 * 
	 */
	String getMessage();

	/**
	 * 
	 */
	void setMessage(String myMessage);

	/**
	 * 
	 */
	void setTwoLineMessage(String msg1, String msg2);

	/**
	 * 
	 */
	void setThreeLineMessage(String msg1, String msg2, String msg3);

	/**
	 * 
	 */
	Photo getPhoto();

	/**
	 * 
	 */
	void setPhoto(Photo newPhoto);

	/**
	 * 
	 */
	Photo getPriorPhoto();

	/**
	 * 
	 */
	void setPriorPhoto(Photo oldPhoto);

	/**
	 * 
	 */
	PhotoCase getPhotoCase();

	/**
	 * 
	 */
	void setPhotoCase(PhotoCase photoCase);

	/**
	 * 
	 */
	String getAndSaveAsString(Map<String, ?> args, String key);

	/**
	 * 
	 */
	String getAsString(Map<String, ?> args, String key);

	/**
	 * 
	 */
	Object getSavedArg(String key);

	/**
	 * 
	 */
	void setSavedArg(String key, Object value);

	/**
	 * 
	 */
	Map<String, Object> getSavedArgs();

	void clearSavedArgs();

}