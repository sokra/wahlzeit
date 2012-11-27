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

package org.wahlzeit.tools;

import java.io.File;
import java.io.FileFilter;

import javax.inject.Inject;
import javax.inject.Named;

import org.wahlzeit.main.AbstractMain;
import org.wahlzeit.main.Main;
import org.wahlzeit.main.MainModule;
import org.wahlzeit.model.ModelModule;
import org.wahlzeit.model.Photo;
import org.wahlzeit.model.PhotoManager;
import org.wahlzeit.model.User;
import org.wahlzeit.model.UserManager;
import org.wahlzeit.utils.StringUtil;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/**
 * 
 * @author dirkriehle
 *
 */
public class CreateUser extends MainModule {
	
	/**
	 * 
	 */
	public static void main(String[] argv) {
		String userName = "testuser";
		String password = "testuser";
		String photoDir = "config/photos";

		for (int i = 0; i < argv.length; i++) {
			String arg = argv[i];
			if (arg.equals("--password")) {
				password = argv[++i];
			} else if (arg.equals("--username")) {
				userName = argv[++i];
			} else if (arg.equals("--photodir")) {
				photoDir = argv[++i];
			}
		}
		
		if (StringUtil.isNullOrEmptyString(password)) {
			password = userName;
		}

		Injector injector = Guice.createInjector(new CreateUser(userName, password, photoDir));
		
		injector.getInstance(Main.class).run();
	}
	
	public CreateUser(String userName, String password, String photoDir) {
		super(false);
		this.userName = userName;
		this.password = password;
		this.photoDir = photoDir;
	}

	/**
	 * 
	 */
	protected final String userName;
	protected final String password;
	protected final String photoDir;
	
	@Override
	protected void configure() {
		super.configure();
		install(new ModelModule());

		bind(Main.class).to(CreateUserMain.class).in(Scopes.SINGLETON);
		
		bind(String.class).annotatedWith(Names.named("userName")).toInstance(userName);
		bind(String.class).annotatedWith(Names.named("password")).toInstance(password);
		bind(String.class).annotatedWith(Names.named("photoDir")).toInstance(photoDir);
	}
	
	static class CreateUserMain extends AbstractMain {
		
		@Inject @Named("userName")
		protected String userName;
		
		@Inject @Named("password")
		protected String password;
		
		@Inject @Named("photoDir")
		protected String photoDir;
		
		@Inject
		protected UserManager userManager;
		
		@Inject
		protected PhotoManager photoManager;
		
		@Inject
		protected User.Factory userFactory;
		
		/**
		 * 
		 */
		protected void execute() throws Exception {
			long confirmationCode = userManager.createConfirmationCode();
			User user = userFactory.create(userName, password, "info@wahlzeit.org", confirmationCode);
			userManager.addUser(user);
			
			File photoDirFile = new File(photoDir);
			FileFilter photoFileFilter = new FileFilter() {
				public boolean accept(File file) {
					return file.getName().endsWith(".jpg");
				}
			};
	
			File[] photoFiles = photoDirFile.listFiles(photoFileFilter);
			for (int i = 0; i < photoFiles.length; i++) {
				Photo newPhoto = photoManager.createPhoto(photoFiles[i]);
				user.addPhoto(newPhoto);
			}
		}
	}
	
}
