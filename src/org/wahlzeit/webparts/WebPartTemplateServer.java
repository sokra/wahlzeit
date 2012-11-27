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

package org.wahlzeit.webparts;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.wahlzeit.services.SysConfig;
import org.wahlzeit.services.SysLog;

/**
 * The WebPartTmplServer creates WebPartTmpls upon request by reading them from disk.
 * It requires configuration with a template directory and uses the following naming convention:
 * tmplDir/language/part-type/part-name.html
 * 
 * @author dirkriehle
 *
 */
public class WebPartTemplateServer {
	
	@Inject
	protected SysLog sysLog;
	
	@Inject
	protected SysConfig sysConfig;

	/**
	 *
	 */
	protected Map<String, WebPartTemplate> templates = new HashMap<String, WebPartTemplate>();

	/**
	 * 
	 */
	public WebPartTemplate getTemplate(String lang, String name) {
		String shortName = lang + File.separator + name;
		WebPartTemplate result = templates.get(shortName);

		if (result == null) {
			try {
				loadTemplate(shortName);
				result = templates.get(shortName);
			} catch (IOException ioex) {
				sysLog.logThrowable(ioex);
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 */
	protected void loadTemplate(String shortName) throws IOException {
		WebPartTemplate template = new WebPartTemplate(shortName);
		String fileName = sysConfig.getTemplatesDir().getFullConfigFileName(shortName + ".html");
		File file = new File(fileName);
		sysLog.logValueWithInfo("file name", fileName, "opened HTML template file");

		FileReader reader = null;
		try {
			reader = new FileReader(file);
			
			// FIXME: Assumes files are always < 50000 bytes
			char[] readBuffer = new char[50000];
			int status = reader.read(readBuffer);

			sysLog.logValueWithInfo("file size", Integer.toString(status), "read HTML template file");
			
			if (status != -1) {
				String source = new String(readBuffer, 0, status);
				template.initialize(source);
				sysLog.logCreatedObject("WebPartTmpl", shortName);
			}

			templates.put(shortName, template);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
}
