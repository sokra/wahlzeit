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

import org.wahlzeit.services.EmailAddress;
import org.wahlzeit.services.Session;

public interface UserSession extends Session, UserSessionTransactionState {

	public static interface Factory {

		UserSession create(String ctxName);

	}

	/**
	 * 
	 */
	void clear();

	/**
	 * 
	 */
	ModelConfig cfg();

	/**
	 * 
	 */
	void setConfiguration(ModelConfig cfg);

	/**
	 * 
	 */
	Client getClient();

	/**
	 * @methodtype set
	 */
	void setClient(Client newClient);

	/**
	 * 
	 */
	String getEmailAddressAsString();

	/**
	 * 
	 */
	void setEmailAddress(EmailAddress emailAddress);

	/**
	 * 
	 */
	PhotoSize getPhotoSize();

	/**
	 * 
	 */
	void setPhotoSize(PhotoSize newPhotoSize);

	/**
	 * 
	 */
	boolean hasConfirmationCode();

	/**
	 * 
	 */
	long getConfirmationCode();

	/**
	 * 
	 */
	void setConfirmationCode(long vc);

	/**
	 * 
	 */
	void clearConfirmationCode();

	/**
	 * 
	 */
	PhotoFilter getPhotoFilter();

	/**
	 * 
	 */
	boolean hasPraisedPhoto(Photo photo);

	/**
	 * 
	 */
	void addPraisedPhoto(Photo photo);

	/**
	 * 
	 */
	void clearPraisedPhotos();

	/**
	 * 
	 */
	void addDisplayedPhoto(Photo photo);

	/**
	 * 
	 */
	void clearDisplayedPhotos();

	/**
	 * 
	 */
	boolean isPhotoOwner(Photo photo);

	/**
	 * 
	 */
	boolean isFormType(Map<String, ?> args, String type);
}