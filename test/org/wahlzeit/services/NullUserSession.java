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

package org.wahlzeit.services;

import java.util.Map;

import org.wahlzeit.model.Client;
import org.wahlzeit.model.ModelConfig;
import org.wahlzeit.model.Photo;
import org.wahlzeit.model.PhotoCase;
import org.wahlzeit.model.PhotoFilter;
import org.wahlzeit.model.PhotoSize;
import org.wahlzeit.model.UserSession;


/**
 * The NullSession does nothing.
 * 
 */
public class NullUserSession extends NullSession implements UserSession {

	@Override
	public void clear() {
	}

	@Override
	public ModelConfig cfg() {
		return null;
	}

	@Override
	public void setConfiguration(ModelConfig cfg) {
	}

	@Override
	public Client getClient() {
		return null;
	}

	@Override
	public void setClient(Client newClient) {
	}

	@Override
	public String getEmailAddressAsString() {
		return null;
	}

	@Override
	public void setEmailAddress(EmailAddress emailAddress) {
	}

	@Override
	public PhotoSize getPhotoSize() {
		return null;
	}

	@Override
	public void setPhotoSize(PhotoSize newPhotoSize) {
	}

	@Override
	public boolean hasConfirmationCode() {
		return false;
	}

	@Override
	public long getConfirmationCode() {
		return 0;
	}

	@Override
	public void setConfirmationCode(long vc) {
	}

	@Override
	public void clearConfirmationCode() {
	}

	@Override
	public PhotoFilter getPhotoFilter() {
		return null;
	}

	@Override
	public boolean hasPraisedPhoto(Photo photo) {
		return false;
	}

	@Override
	public void addPraisedPhoto(Photo photo) {
	}

	@Override
	public void clearPraisedPhotos() {
	}

	@Override
	public void addDisplayedPhoto(Photo photo) {
	}

	@Override
	public void clearDisplayedPhotos() {
	}

	@Override
	public String getHeading() {
		return null;
	}

	@Override
	public void setHeading(String myHeading) {
	}

	@Override
	public String getMessage() {
		return null;
	}

	@Override
	public void setMessage(String myMessage) {
	}

	@Override
	public void setTwoLineMessage(String msg1, String msg2) {
	}

	@Override
	public void setThreeLineMessage(String msg1, String msg2, String msg3) {
	}

	@Override
	public Photo getPhoto() {
		return null;
	}

	@Override
	public void setPhoto(Photo newPhoto) {
	}

	@Override
	public Photo getPriorPhoto() {
		return null;
	}

	@Override
	public void setPriorPhoto(Photo oldPhoto) {
	}

	@Override
	public PhotoCase getPhotoCase() {
		return null;
	}

	@Override
	public void setPhotoCase(PhotoCase photoCase) {
	}

	@Override
	public boolean isPhotoOwner(Photo photo) {
		return false;
	}

	@Override
	public Object getSavedArg(String key) {
		return null;
	}

	@Override
	public void setSavedArg(String key, Object value) {
	}

	@Override
	public Map<String, Object> getSavedArgs() {
		return null;
	}

	@Override
	public void clearSavedArgs() {
	}

	@Override
	public boolean isFormType(Map<String, ?> args, String type) {
		return false;
	}

	@Override
	public String getAsString(Map<String, ?> args, String key) {
		return null;
	}

	@Override
	public String getAndSaveAsString(Map<String, ?> args, String key) {
		return null;
	}

}
