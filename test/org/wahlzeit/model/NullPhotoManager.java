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


/**
 * The NullPhotoManager does nothing.
 * 
 */
public class NullPhotoManager implements PhotoManager {

	@Override
	public boolean hasPhoto(String id) {
		return false;
	}

	@Override
	public boolean hasPhoto(PhotoId id) {
		return false;
	}

	@Override
	public Photo getPhoto(String id) {
		return null;
	}

	@Override
	public Photo getPhoto(PhotoId id) {
		return null;
	}

	@Override
	public Photo getPhotoFromId(PhotoId id) {
		return null;
	}

	@Override
	public void addPhoto(Photo photo) {
	}

	@Override
	public void loadPhotos(Collection<Photo> result) {
	}

	@Override
	public void savePhoto(Photo photo) {
	}

	@Override
	public void savePhotos() {
	}

	@Override
	public Set<Photo> findPhotosByOwner(String ownerName) {
		return null;
	}

	@Override
	public Photo getVisiblePhoto(PhotoFilter filter) {
		return null;
	}

	@Override
	public Photo createPhoto(File file) throws Exception {
		return null;
	}

}
