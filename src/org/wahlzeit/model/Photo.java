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

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.services.DataObject;
import org.wahlzeit.services.EmailAddress;
import org.wahlzeit.services.Language;
import org.wahlzeit.services.persistence.EmailAddressPersistor;
import org.wahlzeit.services.persistence.EnumValuePersistor;
import org.wahlzeit.services.persistence.Persist;
import org.wahlzeit.services.persistence.PhotoIdPersistor;
import org.wahlzeit.services.persistence.TagsPersistor;
import org.wahlzeit.services.persistence.UrlPersistor;
import org.wahlzeit.utils.StringUtil;

/**
 * A photo represents a user-provided (uploaded) photo.
 * 
 * @author dirkriehle
 *
 */
public class Photo extends DataObject {

	/**
	 * 
	 */
	public static final String IMAGE = "image";
	public static final String THUMB = "thumb";
	public static final String LINK = "link";
	public static final String PRAISE = "praise";
	public static final String NO_VOTES = "noVotes";
	public static final String CAPTION = "caption";
	public static final String DESCRIPTION = "description";
	public static final String KEYWORDS = "keywords";

	public static final String TAGS = "tags";

	public static final String STATUS = "status";
	public static final String IS_INVISIBLE = "isInvisible";
	public static final String UPLOADED_ON = "uploadedOn";
	
	/**
	 * 
	 */
	public static final int MAX_PHOTO_WIDTH = 420;
	public static final int MAX_PHOTO_HEIGHT = 600;
	public static final int MAX_THUMB_PHOTO_WIDTH = 105;
	public static final int MAX_THUMB_PHOTO_HEIGHT = 150;
	
	/**
	 * 
	 */
	@Persist(persistor=PhotoIdPersistor.class) protected PhotoId id = null;
	
	/**
	 * 
	 */
	@Persist protected int ownerId = 0;
	@Persist protected String ownerName;

	/**
	 * 
	 */
	@Persist protected boolean ownerNotifyAboutPraise = false;
	@Persist(persistor=EmailAddressPersistor.class) protected EmailAddress ownerEmailAddress = EmailAddress.EMPTY;
	@Persist(persistor=EnumValuePersistor.class) protected Language ownerLanguage = Language.ENGLISH;
	@Persist(persistor=UrlPersistor.class) protected URL ownerHomePage;
	
	/**
	 * 
	 */
	@Persist protected int width;
	@Persist protected int height;
	protected PhotoSize maxPhotoSize = PhotoSize.MEDIUM; // derived
	
	/**
	 * 
	 */
	@Persist(persistor=TagsPersistor.class) protected Tags tags = Tags.EMPTY_TAGS;

	/**
	 * 
	 */
	@Persist(persistor=EnumValuePersistor.class) protected PhotoStatus status = PhotoStatus.VISIBLE;
	
	/**
	 * 
	 */
	@Persist protected int praiseSum = 10;
	@Persist protected int noVotes = 1;
	
	/**
	 * 
	 */
	@Persist protected long creationTime = System.currentTimeMillis();
	
	/**
	 * 
	 */
	public Photo() {
		id = PhotoId.getNextId();
		incWriteCount();
	}
	
	/**
	 * 
	 * @methodtype constructor
	 */
	public Photo(PhotoId myId) {
		id = myId;
		
		incWriteCount();
	}
	
	/**
	 * 
	 * @methodtype constructor
	 */
	public Photo(ResultSet rset) throws SQLException {
		readFrom(rset);
	}

	/**
	 * 
	 * @methodtype get
	 */
	public String getIdAsString() {
		return String.valueOf(id.asInt());
	}
	
	/**
	 * 
	 */
	public void readFrom(ResultSet rset) throws SQLException {
		super.readFrom(rset);
		maxPhotoSize = PhotoSize.getFromWidthHeight(width, height);
	}

	/**
	 * 
	 */
	public void writeId(PreparedStatement stmt, int pos) throws SQLException {
		stmt.setInt(pos, id.asInt());
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public PhotoId getId() {
		return id;
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public int getOwnerId() {
		return ownerId;
	}
	
	/**
	 * 
	 * @methodtype set
	 */
	public void setOwnerId(int newId) {
		ownerId = newId;
		incWriteCount();
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public String getOwnerName() {
		return ownerName;
	}
	
	/**
	 * 
	 * @methodtype set
	 */
	public void setOwnerName(String newName) {
		ownerName = newName;
		incWriteCount();
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public String getSummary(ModelConfig cfg) {
		return cfg.asPhotoSummary(ownerName);
	}

	/**
	 * 
	 * @methodtype get
	 */
	public String getCaption(ModelConfig cfg) {
		return cfg.asPhotoCaption(ownerName, ownerHomePage);
	}

	/**
	 * 
	 * @methodtype get
	 */
	public boolean getOwnerNotifyAboutPraise() {
		return ownerNotifyAboutPraise;
	}
	
	/**
	 * 
	 * @methodtype set
	 */
	public void setOwnerNotifyAboutPraise(boolean newNotifyAboutPraise) {
		ownerNotifyAboutPraise = newNotifyAboutPraise;
		incWriteCount();
	}

	/**
	 * 
	 * @methodtype get
	 */
	public EmailAddress getOwnerEmailAddress() {
		return ownerEmailAddress;
	}
	
	/**
	 * 
	 * @methodtype set
	 */
	public void setOwnerEmailAddress(EmailAddress newEmailAddress) {
		ownerEmailAddress = newEmailAddress;
		incWriteCount();
	}

	/**
	 * 
	 */
	public Language getOwnerLanguage() {
		return ownerLanguage;
	}
	
	/**
	 * 
	 */
	public void setOwnerLanguage(Language newLanguage) {
		ownerLanguage = newLanguage;
		incWriteCount();
	}

	/**
	 * 
	 * @methodtype get
	 */
	public URL getOwnerHomePage() {
		return ownerHomePage;
	}
	
	/**
	 * 
	 * @methodtype set
	 */
	public void setOwnerHomePage(URL newHomePage) {
		ownerHomePage = newHomePage;
		incWriteCount();
	}
	
	/**
	 * 
	 * @methodtype boolean-query
	 */
	public boolean hasSameOwner(Photo photo) {
		return photo.getOwnerEmailAddress().equals(ownerEmailAddress);
	}

	/**
	 * 
	 * @methodtype boolean-query
	 */
	public boolean isWiderThanHigher() {
		return (height * MAX_PHOTO_WIDTH) < (width * MAX_PHOTO_HEIGHT);
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public int getThumbWidth() {
		return isWiderThanHigher() ? MAX_THUMB_PHOTO_WIDTH : (width * MAX_THUMB_PHOTO_HEIGHT / height);
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public int getThumbHeight() {
		return isWiderThanHigher() ? (height * MAX_THUMB_PHOTO_WIDTH / width) : MAX_THUMB_PHOTO_HEIGHT;
	}
	
	/**
	 * 
	 * @methodtype set
	 */
	public void setWidthAndHeight(int newWidth, int newHeight) {
		width = newWidth;
		height = newHeight;

		maxPhotoSize = PhotoSize.getFromWidthHeight(width, height);

		incWriteCount();
	}
	
	/**
	 * Can this photo satisfy provided photo size?
	 * 
	 * @methodtype boolean-query
	 */
	public boolean hasPhotoSize(PhotoSize size) {
		return maxPhotoSize.asInt() >= size.asInt();
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public PhotoSize getMaxPhotoSize() {
		return maxPhotoSize;
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public double getPraise() {
		return (double) praiseSum / noVotes;
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public String getPraiseAsString(ModelConfig cfg) {
		return cfg.asPraiseString(getPraise());
	}
	
	/**
	 * 
	 */
	public void addToPraise(int value) {
		praiseSum += value;
		noVotes += 1;
		incWriteCount();
	}
	
	/**
	 * 
	 * @methodtype boolean-query
	 */
	public boolean isVisible() {
		return status.isDisplayable();
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public PhotoStatus getStatus() {
		return status;
	}
	
	/**
	 * 
	 * @methodtype set
	 */
	public void setStatus(PhotoStatus newStatus) {
		status = newStatus;
		incWriteCount();
	}
	
	/**
	 * 
	 * @methodtype boolean-query
	 */
	public boolean hasTag(String tag) {
		return tags.hasTag(tag);
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public Tags getTags() {
		return tags;
	}

	/**
	 * 
	 * @methodtype set
	 */
	public void setTags(Tags newTags) {
		tags = newTags;
		incWriteCount();
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public long getCreationTime() {
		return creationTime;
	}
	
}
