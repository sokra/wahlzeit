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