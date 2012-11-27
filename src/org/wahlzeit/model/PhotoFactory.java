package org.wahlzeit.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface PhotoFactory {

	/**
	 * @methodtype factory
	 */
	Photo createPhoto();

	/**
	 * 
	 */
	Photo createPhoto(PhotoId id);

	/**
	 * 
	 */
	Photo createPhoto(ResultSet rs) throws SQLException;

	/**
	 * 
	 */
	PhotoFilter createPhotoFilter();

	/**
	 * 
	 */
	PhotoTagCollector createPhotoTagCollector();

}