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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.wahlzeit.services.SysConfig;
import org.wahlzeit.services.SysLog;

/**
 * PhotoUtil provides a set of utility functions to create defined images.
 * Images are created from a source in different sizes as needed by the app.
 * 
 * @author dirkriehle
 *
 */
public class PhotoUtil {
	
	@Inject
	protected SysLog sysLog;
	
	@Inject
	protected SysConfig sysConfig;
	
	private final PhotoFactory photoFactory;

	@Inject
	public PhotoUtil(PhotoFactory photoFactory) {
		this.photoFactory = photoFactory;
	}
	
	/**
	 * 
	 */
	public Photo createPhoto(File source, PhotoId id) throws Exception {
		Photo result = photoFactory.createPhoto(id);
		
		Image sourceImage = createImageFiles(source, id);

		int sourceWidth = sourceImage.getWidth(null);
		int sourceHeight = sourceImage.getHeight(null);
		result.setWidthAndHeight(sourceWidth, sourceHeight);

		return result;
	}
	
	/**
	 * 
	 */
	public Image createImageFiles(File source, PhotoId id) throws Exception {
		Image sourceImage = ImageIO.read(source);
		assertIsValidImage(sourceImage);

		int sourceWidth = sourceImage.getWidth(null);
		int sourceHeight = sourceImage.getHeight(null);
		assertHasValidSize(sourceWidth, sourceHeight);
		
		for (PhotoSize size : PhotoSize.values()) {
			if (!size.isWiderAndHigher(sourceWidth, sourceHeight)) {
				createImageFile(sourceImage, id, size);
			}
		}
		
		return sourceImage;
	}
	
	/**
	 * 
	 */
	protected void createImageFile(Image source, PhotoId id, PhotoSize size) throws Exception {	
		int sourceWidth = source.getWidth(null);
		int sourceHeight = source.getHeight(null);
		
		int targetWidth = size.calcAdjustedWidth(sourceWidth, sourceHeight);
		int targetHeight = size.calcAdjustedHeight(sourceWidth, sourceHeight);

		BufferedImage targetImage = scaleImage(source, targetWidth, targetHeight);
		File target = new File(sysConfig.getPhotosDirAsString() + id.asString() + size.asInt() + ".jpg");
		ImageIO.write(targetImage, "jpg", target);

		sysLog.logInfo("created image file for id: " + id.asString() + " of size: " + size.asString());
	}

	/**
	 * 
	 */
	protected BufferedImage scaleImage(Image source, int width, int height) {
		source = source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = result.createGraphics();
		g2d.setBackground(Color.WHITE);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.drawImage(source, 0, 0, null);
		return result;
	}
	
	/**
	 * @methodtype assertion 
	 */
	protected void assertIsValidImage(Image image) {
		if (image == null) {
			throw new IllegalArgumentException("Not a valid photo!");
		}
	}

	/**
	 * 
	 */
	protected void assertHasValidSize(int cw, int ch) {
		if (PhotoSize.THUMB.isWiderAndHigher(cw, ch)) {
			throw new IllegalArgumentException("Photo too small!");
		}
	}

}
