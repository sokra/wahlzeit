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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.wahlzeit.services.ObjectManager;
import org.wahlzeit.services.Persistent;
import org.wahlzeit.services.Session;
import org.wahlzeit.services.SysLog;

/**
 * A photo manager provides access to and manages photos.
 * 
 * @author dirkriehle
 * 
 */
public class PhotoManagerImpl extends ObjectManager implements Saveable, PhotoManager {
	
	/**
	 * In-memory cache for photos
	 */
	protected Map<PhotoId, Photo> photoCache = new HashMap<PhotoId, Photo>();
	
	/**
	 * 
	 */
	protected final PhotoTagCollector photoTagCollector;
	
	protected final PhotoFactory photoFactory;

	protected final PhotoUtil photoUtil;
	
	/**
	 * 
	 */
	@Override
	public final boolean hasPhoto(String id) {
		return hasPhoto(PhotoId.getId(id));
	}
	
	/**
	 * 
	 */
	@Override
	public final boolean hasPhoto(PhotoId id) {
		return getPhoto(id) != null;
	}
	
	/**
	 * 
	 */
	@Override
	public final Photo getPhoto(String id) {
		return getPhoto(PhotoId.getId(id));
	}
	
	/**
	 * 
	 */
	@Override
	public final Photo getPhoto(PhotoId id) {
		return getPhotoFromId(id);
	}
	
	/**
	 * 
	 */
	@Inject
	public PhotoManagerImpl(SysLog sysLog, Provider<Session> contextProvider, 
			PhotoFactory photoFactory, PhotoTagCollector photoTagCollector, PhotoUtil photoUtil) {
		super(sysLog, contextProvider);
		this.photoTagCollector = photoTagCollector;
		this.photoFactory = photoFactory;
		this.photoUtil = photoUtil;
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
	@Override
	public Photo getPhotoFromId(PhotoId id) {
		if (id.isNullId()) {
			return null;
		}

		Photo result = doGetPhotoFromId(id);
		
		if (result == null) {
			try {
				result = (Photo) readObject(getReadingStatement("SELECT * FROM photos WHERE id = ?"), id.asInt());
			} catch (SQLException sex) {
				sysLog.logThrowable(sex);
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
		return photoFactory.createPhoto(rset);
	}
	
	/**
	 * 
	 */
	@Override
	public void addPhoto(Photo photo) {
		PhotoId id = photo.getId();
		assertIsNewPhoto(id);
		doAddPhoto(photo);

		try {
			createObject(photo, getReadingStatement("INSERT INTO photos(id) VALUES(?)"), id.asInt());
		} catch (SQLException sex) {
			sysLog.logThrowable(sex);
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
	@Override
	public void loadPhotos(Collection<Photo> result) {
		try {
			readObjects(result, getReadingStatement("SELECT * FROM photos"));
			for (Iterator<Photo> i = result.iterator(); i.hasNext(); ) {
				Photo photo = i.next();
				if (!doHasPhoto(photo.getId())) {
					doAddPhoto(photo);
				} else {
					sysLog.logValueWithInfo("photo", photo.getId().asString(), "photo had already been loaded");
				}
			}
		} catch (SQLException sex) {
			sysLog.logThrowable(sex);
		}
		
		sysLog.logInfo("loaded all photos");
	}
	
	/**
	 * 
	 */
	@Override
	public void savePhoto(Photo photo) {
		try {
			updateObject(photo, getUpdatingStatement("SELECT * FROM photos WHERE id = ?"));
		} catch (SQLException sex) {
			sysLog.logThrowable(sex);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void savePhotos() {
		try {
			updateObjects(photoCache.values(), getUpdatingStatement("SELECT * FROM photos WHERE id = ?"));
		} catch (SQLException sex) {
			sysLog.logThrowable(sex);
		}
	}
	
	@Override
	public void save() {
		savePhotos();
	}
	
	/**
	 * 
	 */
	@Override
	public Set<Photo> findPhotosByOwner(String ownerName) {
		Set<Photo> result = new HashSet<Photo>();
		try {
			readObjects(result, getReadingStatement("SELECT * FROM photos WHERE owner_name = ?"), ownerName);
		} catch (SQLException sex) {
			sysLog.logThrowable(sex);
		}
		
		for (Iterator<Photo> i = result.iterator(); i.hasNext(); ) {
			doAddPhoto(i.next());
		}

		return result;
	}
	
	/**
	 * 
	 */
	@Override
	public Photo getVisiblePhoto(PhotoFilter filter) {
		Photo result = getPhotoFromFilter(filter);
		
		if(result == null) {
			java.util.List<PhotoId> list = getFilteredPhotoIds(filter);
			filter.setDisplayablePhotoIds(list);
			result = getPhotoFromFilter(filter);
		}

		return result;
	}
	
	/**
	 * 
	 */
	protected Photo getPhotoFromFilter(PhotoFilter filter) {
		PhotoId id = filter.getRandomDisplayablePhotoId();
		Photo result = getPhotoFromId(id);
		while((result != null) && !result.isVisible()) {
			id = filter.getRandomDisplayablePhotoId();
			result = getPhotoFromId(id);
			if ((result != null) && !result.isVisible()) {
				filter.addProcessedPhoto(result);
			}
		}
		
		return result;
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
			
			sysLog.logQuery(stmt);
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
			sysLog.logThrowable(sex);
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
		
		deleteObject(obj, getReadingStatement("DELETE FROM tags WHERE photo_id = ?"));
		
		PreparedStatement stmt = getReadingStatement("INSERT INTO tags VALUES(?, ?)");
		
		Set<String> tags = new HashSet<String>();
		photoTagCollector.collect(tags, photo);
		for (Iterator<String> i = tags.iterator(); i.hasNext(); ) {
			String tag = i.next();
			stmt.setString(1, tag);
			stmt.setInt(2, photo.getId().asInt());
			sysLog.logQuery(stmt);
			stmt.executeUpdate();					
		}
	}
		
	/**
	 * 
	 */
	@Override
	public Photo createPhoto(File file) throws Exception {
		PhotoId id = PhotoId.getNextId();
		Photo result = photoUtil.createPhoto(file, id);
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

}
