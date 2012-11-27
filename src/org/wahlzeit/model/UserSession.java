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