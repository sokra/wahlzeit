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

import javax.inject.Inject;

import org.wahlzeit.model.Client;
import org.wahlzeit.model.Guest;
import org.wahlzeit.model.ModelConfig;
import org.wahlzeit.model.Photo;
import org.wahlzeit.model.PhotoCase;
import org.wahlzeit.model.UserSessionTransactionState;
import org.wahlzeit.model.UserSessionTransactionStateImpl;


/**
 * The NullSession does nothing.
 * 
 */
public class MockUserSession extends NullUserSession {
	
	@Inject
	protected ModelConfig modelConfig;
	
	@Override
	public ModelConfig cfg() {
		return modelConfig;
	}
	
	public static final String EMAIL_TEST_VALUE = "test@email.adr";

	@Override
	public String getEmailAddressAsString() {
		return EMAIL_TEST_VALUE;
	}
	
	protected Client client = new Guest();
	
	@Override
	public Client getClient() {
		return client;
	}
	
	protected UserSessionTransactionState transactionsState = new UserSessionTransactionStateImpl();
	
	@Override
	public String getHeading() {
		return transactionsState.getHeading();
	}

	@Override
	public void setHeading(String myHeading) {
		transactionsState.setHeading(myHeading);
	}

	@Override
	public String getMessage() {
		return transactionsState.getMessage();
	}

	@Override
	public void setMessage(String myMessage) {
		transactionsState.setMessage(myMessage);
	}

	@Override
	public void setTwoLineMessage(String msg1, String msg2) {
		transactionsState.setTwoLineMessage(msg1, msg2);
	}

	@Override
	public void setThreeLineMessage(String msg1, String msg2, String msg3) {
		transactionsState.setThreeLineMessage(msg1, msg2, msg3);
	}

	@Override
	public Photo getPhoto() {
		return transactionsState.getPhoto();
	}

	@Override
	public void setPhoto(Photo newPhoto) {
		transactionsState.setPhoto(newPhoto);
	}

	@Override
	public Photo getPriorPhoto() {
		return transactionsState.getPriorPhoto();
	}

	@Override
	public void setPriorPhoto(Photo oldPhoto) {
		transactionsState.setPriorPhoto(oldPhoto);
	}

	@Override
	public PhotoCase getPhotoCase() {
		return transactionsState.getPhotoCase();
	}

	@Override
	public void setPhotoCase(PhotoCase photoCase) {
		transactionsState.setPhotoCase(photoCase);
	}

	@Override
	public String getAndSaveAsString(Map<String, ?> args, String key) {
		return transactionsState.getAndSaveAsString(args, key);
	}

	@Override
	public String getAsString(Map<String, ?> args, String key) {
		return transactionsState.getAsString(args, key);
	}

	@Override
	public Object getSavedArg(String key) {
		return transactionsState.getSavedArgs();
	}

	@Override
	public void setSavedArg(String key, Object value) {
		transactionsState.setSavedArg(key, value);
	}

	@Override
	public Map<String, Object> getSavedArgs() {
		return transactionsState.getSavedArgs();
	}

	@Override
	public void clearSavedArgs() {
		transactionsState.clearSavedArgs();
	}

}
