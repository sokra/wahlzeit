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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.wahlzeit.services.AbstractSession;
import org.wahlzeit.services.EmailAddress;
import org.wahlzeit.services.Language;
import org.wahlzeit.utils.StringUtil;

import com.google.inject.Injector;

/**
 * 
 * @author dirkriehle
 *
 */
public class UserSessionImpl extends AbstractSession implements UserSession {
	
	@Inject
	protected PhotoFactory photoFactory;
	
	@Inject
	protected LanguageConfigs languageConfigs;

	/**
	 * Session state
	 */
	protected ModelConfig configuration;

	protected Client client = new Guest();
	protected PhotoSize photoSize = PhotoSize.MEDIUM;
	protected long confirmationCode = -1; // -1 means not set
	protected PhotoFilter photoFilter;
	protected Set<Photo> praisedPhotos = new HashSet<Photo>();

	protected UserSessionTransactionStateImpl transactionsState = new UserSessionTransactionStateImpl();

	/**
	 * 
	 */
	@Inject
	protected UserSessionImpl() {
	}
	
	@Override
	protected void initialize(String ctxName) {
		super.initialize(ctxName);
		configuration = languageConfigs.get(Language.ENGLISH);
		photoFilter = photoFactory.createPhotoFilter();
	}
	
	/**
	 * 
	 */
	@Override
	public void clear() {
		configuration = languageConfigs.get(Language.ENGLISH);
		photoSize = PhotoSize.MEDIUM;
		clearDisplayedPhotos();
		clearPraisedPhotos();
	}
	
	/**
	 * 
	 */
	@Override
	public ModelConfig cfg() {
		return configuration;
	}
	
	/**
	 * 
	 */
	@Override
	public void setConfiguration(ModelConfig cfg) {
		configuration = cfg;
	}
	
	/**
	 * 
	 */
	@Override
	public Client getClient() {
		return client;
	}
	
	/**
	 * @methodtype set
	 */
	@Override
	public void setClient(Client newClient) {
		client = newClient;
	}
	
	/**
	 * Returns some signifier of current user
	 */
	public String getClientName() {
		String result = "anon";
		if (!StringUtil.isNullOrEmptyString(getEmailAddressAsString())) {
			result = getEmailAddressAsString();
			if (client instanceof User) {
				User user = (User) client;
				result = user.getName();
			} 
		}
		return result;
	}
	
	/**
	 * 
	 */
	@Override
	public String getEmailAddressAsString() {
		String result = null;
		if (client != null) {
			result = client.getEmailAddress().asString();
		}
		return result;
	}
	
	/**
	 * 
	 */
	@Override
	public void setEmailAddress(EmailAddress emailAddress) {
		if (client != null) {
			client.setEmailAddress(emailAddress);
		} else {
			sysLog.logError("attempted to set email address to null client");
		}
	}
	
	/**
	 * 
	 */
	@Override
	public PhotoSize getPhotoSize() {
		return photoSize;
	}
	
	/**
	 * 
	 */
	@Override
	public void setPhotoSize(PhotoSize newPhotoSize) {
		photoSize = newPhotoSize;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean hasConfirmationCode() {
		return confirmationCode != -1;
	}
	
	/**
	 * 
	 */
	@Override
	public long getConfirmationCode() {
		return confirmationCode;
	}
	
	/**
	 * 
	 */
	@Override
	public void setConfirmationCode(long vc) {
		confirmationCode = vc;
	}
	
	/**
	 * 
	 */
	@Override
	public void clearConfirmationCode() {
		confirmationCode = -1;
	}
	
	/**
	 * 
	 */
	@Override
	public PhotoFilter getPhotoFilter() {
		return photoFilter;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean hasPraisedPhoto(Photo photo) {
		return praisedPhotos.contains(photo);
	}
	
	/**
	 * 
	 */
	@Override
	public void addPraisedPhoto(Photo photo) {
		praisedPhotos.add(photo);
	}
	
	/**
	 * 
	 */
	@Override
	public void clearPraisedPhotos() {
		praisedPhotos.clear();
	}
	
	/**
	 * 
	 */
	@Override
	public void addDisplayedPhoto(Photo photo) {
		photoFilter.addProcessedPhoto(photo);
	}
	
	/**
	 * 
	 */
	@Override
	public void clearDisplayedPhotos() {
		photoFilter.clear();
	}
	
	/**
	 * 
	 */
	@Override
	public boolean isPhotoOwner(Photo photo) {
		boolean result = false;
		Client client = getClient();
		if ((photo != null) && (client instanceof User)) {
			User user = (User) client;
			result = photo.getOwnerName().equals(user.getName());
		}
		return result;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean isFormType(Map<String, ?> args, String type) {
		Object value = args.get(type);
		return (value != null) && !value.equals("");
	}

	public static class Factory implements UserSession.Factory {
		
		@Inject
		protected Injector injector;
		
		@Override
		public UserSession create(String ctxName) {
			UserSessionImpl session = injector.getInstance(UserSessionImpl.class);
			session.initialize(ctxName);
			return session;
		}
		
	}

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
