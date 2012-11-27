package org.wahlzeit.model;

import java.util.Collection;

public interface PhotoCaseManager {

	/**
	 * 
	 * @methodtype get
	 */
	PhotoCase getPhotoCase(int id);

	/**
	 * 
	 * @methodtype command
	 */
	void addPhotoCase(PhotoCase myCase);

	/**
	 * 
	 * @methodtype command
	 */
	void removePhotoCase(PhotoCase myCase);

	/**
	 * 
	 * @methodtype command
	 */
	void loadOpenPhotoCases(Collection<PhotoCase> result);

	/**
	 * 
	 * @methodtype command
	 */
	void savePhotoCases();

	/**
	 * 
	 * @methodtype get
	 */
	PhotoCase[] getOpenPhotoCasesByAscendingAge();

}