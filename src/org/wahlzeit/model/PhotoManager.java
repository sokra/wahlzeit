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