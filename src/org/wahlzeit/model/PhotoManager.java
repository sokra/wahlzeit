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

import java.io.*;
import java.sql.*;
import java.util.*;

import org.wahlzeit.main.*;
import org.wahlzeit.services.*;

/**
 * A photo manager provides access to and manages photos.
 * 
 * @author dirkriehle
 * 
 */
public class PhotoManager extends ObjectManager {
	
	/**
	 * 
	 */
	protected static final PhotoManager instance = new PhotoManager();

	/**
	 * In-memory cache for photos
	 */
	protected Map<PhotoId, Photo> photoCache = new HashMap<PhotoId, Photo>();
	
	/**
	 * 
	 */
	protected PhotoTagCollector photoTagCollector = null;
	
	/**
	 * 
	 */
	public static final PhotoManager getInstance() {
		return instance;
	}
	
	/**
	 * 
	 */
	public static final boolean hasPhoto(String id) {
		return hasPhoto(PhotoId.getId(id));
	}
	
	/**
	 * 
	 */
	public static final boolean hasPhoto(PhotoId id) {
		try {
			return getPhoto(id) != null;
		} catch (PhotoNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * 
	 */
	public static final Photo getPhoto(String id) throws PhotoNotFoundException {
		return getPhoto(PhotoId.getId(id));
	}
	
	/**
	 * 
	 */
	public static final Photo getPhoto(PhotoId id) throws PhotoNotFoundException {
		return instance.getPhotoFromId(id);
	}
	
	/**
	 * 
	 */
	public PhotoManager() {
		photoTagCollector = PhotoFactory.getInstance().createPhotoTagCollector();
	}
	
	/**
	 * @methodtype boolean-query
	 * @methodproperties primitive
	 */
	protected boolean doHasPhoto(PhotoId id) {
		return photoCache.containsKey(id);
	}
	
	/**
	 * 
	 */
	public Photo getPhotoFromId(PhotoId id) throws PhotoNotFoundException {
		if (id.isNullId()) {
			throw new PhotoNotFoundException(id);
		}

		Photo result = doGetPhotoFromId(id);
		
		if (result == null) {
			try {
				PreparedStatement stmt = getReadingStatement("SELECT * FROM photos WHERE id = ?");
				result = (Photo) readObject(stmt, id.asInt());
			} catch (SQLException sex) {
				SysLog.logThrowable(sex);
				throw new PhotoNotFoundException(id, sex);
			}
			if (result != null) {
				doAddPhoto(result);
			}
		}
		
		return result;
	}
		
	/**
	 * @methodtype get
	 * @methodproperties primitive
	 */
	protected Photo doGetPhotoFromId(PhotoId id) {
		return photoCache.get(id);
	}
	
	/**
	 * 
	 */
	protected Photo createObject(ResultSet rset) throws SQLException {
		return PhotoFactory.getInstance().createPhoto(rset);
	}
	
	/**
	 * 
	 */
	public void addPhoto(Photo photo) {
		PhotoId id = photo.getId();
		assertIsNewPhoto(id);
		doAddPhoto(photo);

		try {
			PreparedStatement stmt = getReadingStatement("INSERT INTO photos(id) VALUES(?)");
			createObject(photo, stmt, id.asInt());
			Wahlzeit.saveGlobals();
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
	}
	
	/**
	 * @methodtype command
	 * @methodproperties primitive
	 */
	protected void doAddPhoto(Photo myPhoto) {
		photoCache.put(myPhoto.getId(), myPhoto);
	}

	/**
	 * 
	 */
	public void loadPhotos(Collection<Photo> result) {
		try {
			PreparedStatement stmt = getReadingStatement("SELECT * FROM photos");
			readObjects(result, stmt);
			for (Iterator<Photo> i = result.iterator(); i.hasNext(); ) {
				Photo photo = i.next();
				if (!doHasPhoto(photo.getId())) {
					doAddPhoto(photo);
				} else {
					SysLog.logValueWithInfo("photo", photo.getId().asString(), "photo had already been loaded");
				}
			}
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		SysLog.logInfo("loaded all photos");
	}
	
	/**
	 * 
	 */
	public void savePhoto(Photo photo) {
		try {
			PreparedStatement stmt = getUpdatingStatement("SELECT * FROM photos WHERE id = ?");
			updateObject(photo, stmt);
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
	}
	
	/**
	 * 
	 */
	public void savePhotos() {
		try {
			PreparedStatement stmt = getUpdatingStatement("SELECT * FROM photos WHERE id = ?");
			updateObjects(photoCache.values(), stmt);
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
	}
	
	/**
	 * 
	 */
	public Set<Photo> findPhotosByOwner(String ownerName) {
		Set<Photo> result = new HashSet<Photo>();
		try {
			PreparedStatement stmt = getReadingStatement("SELECT * FROM photos WHERE owner_name = ?");
			readObjects(result, stmt, ownerName);
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		for (Iterator<Photo> i = result.iterator(); i.hasNext(); ) {
			doAddPhoto(i.next());
		}

		return result;
	}
	
	/**
	 * 
	 */
	public Photo getVisiblePhoto(PhotoFilter filter) throws NoPhotoMatchesFilterException {
		try {
			return getPhotoFromFilter(filter);
		} catch(NoPhotoMatchesFilterException e) {
			java.util.List<PhotoId> list = getFilteredPhotoIds(filter);
			filter.setDisplayablePhotoIds(list);
			return getPhotoFromFilter(filter);
		}
	}
	
	/**
	 * 
	 */
	protected Photo getPhotoFromFilter(PhotoFilter filter) throws NoPhotoMatchesFilterException {
		try {
			PhotoId id = filter.getRandomDisplayablePhotoId();
			Photo result = getPhotoFromId(id);
			while(!result.isVisible()) {
				id = filter.getRandomDisplayablePhotoId();
				result = getPhotoFromId(id);
				if (!result.isVisible()) {
					filter.addProcessedPhoto(result);
				}
			}
			
			return result;
		} catch(PhotoNotFoundException e) {
			throw new NoPhotoMatchesFilterException(filter, e.getCause());
		}
	}
	
	/**
	 * 
	 */
	protected java.util.List<PhotoId> getFilteredPhotoIds(PhotoFilter filter) {
		java.util.List<PhotoId> result = new LinkedList<PhotoId>();

		try {
			java.util.List<String> filterConditions = filter.getFilterConditions();

			int noFilterConditions = filterConditions.size();
			PreparedStatement stmt = getUpdatingStatementFromConditions(noFilterConditions);
			for (int i = 0; i < noFilterConditions; i++) {
				stmt.setString(i + 1, filterConditions.get(i));
			}
			
			SysLog.logQuery(stmt);
			ResultSet rset = stmt.executeQuery();

			if (noFilterConditions == 0) {
				noFilterConditions++;
			}

			int[] ids = new int[PhotoId.getValue() + 1];
			while(rset.next()) {
				int id = rset.getInt("photo_id");
				if (++ids[id] == noFilterConditions) {
					PhotoId photoId = PhotoId.getId(id);
					if (!filter.isProcessedPhotoId(photoId)) {
						result.add(photoId);
					}
				}
			}
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		return result;
	}
		
	/**
	 * 
	 */
	protected PreparedStatement getUpdatingStatementFromConditions(int no) throws SQLException {
		String query = "SELECT * FROM tags";
		if (no > 0) {
			query += " WHERE";
		}

		for (int i = 0; i < no; i++) {
			if (i > 0) {
				query += " OR";
			}
			query += " (tag = ?)";
		}
		
		return getUpdatingStatement(query);
	}
	
	/**
	 * 
	 */
	protected void updateDependents(Persistent obj) throws SQLException {
		Photo photo = (Photo) obj;
		
		PreparedStatement stmt = getReadingStatement("DELETE FROM tags WHERE photo_id = ?");
		deleteObject(obj, stmt);
		
		stmt = getReadingStatement("INSERT INTO tags VALUES(?, ?)");
		Set<String> tags = new HashSet<String>();
		photoTagCollector.collect(tags, photo);
		for (Iterator<String> i = tags.iterator(); i.hasNext(); ) {
			String tag = i.next();
			stmt.setString(1, tag);
			stmt.setInt(2, photo.getId().asInt());
			SysLog.logQuery(stmt);
			stmt.executeUpdate();					
		}
	}
		
	/**
	 * 
	 */
	public Photo createPhoto(File file) throws Exception {
		PhotoId id = PhotoId.getNextId();
		Photo result = PhotoUtil.createPhoto(file, id);
		addPhoto(result);
		return result;
	}
	
	/**
	 * @methodtype assertion
	 */
	protected void assertIsNewPhoto(PhotoId id) {
		if (hasPhoto(id)) {
			throw new IllegalStateException("Photo already exists!");
		}
	}
	
	/**
	 * 
	 */
	public static class PhotoNotFoundException extends Exception {
		private static final long serialVersionUID = -1051890570071191390L;

		private PhotoId id;

		public PhotoNotFoundException(PhotoId id, SQLException cause) {
			super("Photo " + id.toString() + " not found", cause);
			this.id = id;
		}
		
		public PhotoNotFoundException(PhotoId id) {
			super("Photo " + id.toString() + " not found");
			this.id = id;
		}

		public PhotoId getId() {
			return id;
		}
		
		@Override
		public synchronized SQLException getCause() {
			return (SQLException) super.getCause();
		}
		
	}
	
	/**
	 * 
	 */
	public static class NoPhotoMatchesFilterException extends Exception {
		private static final long serialVersionUID = -3134176764971224009L;

		private PhotoFilter filter;
		
		public NoPhotoMatchesFilterException(PhotoFilter filter, SQLException cause) {
			super("No photo matches the filter", cause);
			this.filter = filter;
		}
		
		public PhotoFilter getFilter() {
			return filter;
		}
		
		@Override
		public synchronized SQLException getCause() {
			return (SQLException) super.getCause();
		}
		
	}
}
