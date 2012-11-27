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

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import com.google.inject.Injector;

/**
 * 
 * @author dirkriehle
 *
 */

public class PhotoFactory {
	
	private final Injector injector;

	/**
	 * 
	 */
	@Inject
	protected PhotoFactory(Injector injector) {
		this.injector = injector;
	}

	/**
	 * @methodtype factory
	 */
	public Photo createPhoto() {
		Photo photo = new Photo();
		injector.injectMembers(photo);
		return photo;
	}
	
	/**
	 * 
	 */
	public Photo createPhoto(PhotoId id) {
		Photo photo = new Photo(id);
		injector.injectMembers(photo);
		return photo;
	}
	
	/**
	 * 
	 */
	public Photo createPhoto(ResultSet rs) throws SQLException {
		Photo photo = new Photo(rs);
		injector.injectMembers(photo);
		return photo;
	}
	
	/**
	 * 
	 */
	public PhotoFilter createPhotoFilter() {
		PhotoFilter photoFilter = new PhotoFilter();
		injector.injectMembers(photoFilter);
		return photoFilter;
	}
	
	/**
	 * 
	 */
	public PhotoTagCollector createPhotoTagCollector() {
		PhotoTagCollector photoTagCollector = new PhotoTagCollector();
		injector.injectMembers(photoTagCollector);
		return photoTagCollector;
	}

}
