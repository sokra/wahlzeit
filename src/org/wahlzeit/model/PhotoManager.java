package org.wahlzeit.model;

import java.io.File;
import java.util.Collection;
import java.util.Set;

public interface PhotoManager {

	/**
	 * 
	 */
	boolean hasPhoto(String id);

	/**
	 * 
	 */
	boolean hasPhoto(PhotoId id);

	/**
	 * 
	 */
	Photo getPhoto(String id);

	/**
	 * 
	 */
	Photo getPhoto(PhotoId id);

	/**
	 * 
	 */
	Photo getPhotoFromId(PhotoId id);

	/**
	 * 
	 */
	void addPhoto(Photo photo);

	/**
	 * 
	 */
	void loadPhotos(Collection<Photo> result);

	/**
	 * 
	 */
	void savePhoto(Photo photo);

	/**
	 * 
	 */
	void savePhotos();

	/**
	 * 
	 */
	Set<Photo> findPhotosByOwner(String ownerName);

	/**
	 * 
	 */
	Photo getVisiblePhoto(PhotoFilter filter);

	/**
	 * 
	 */
	Photo createPhoto(File file) throws Exception;

}