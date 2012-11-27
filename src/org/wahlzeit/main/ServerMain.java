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

package org.wahlzeit.main;

import java.io.IOException;

import javax.inject.Inject;

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.NotFoundHandler;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.wahlzeit.handlers.AdminUserPhotoFormHandler;
import org.wahlzeit.handlers.AdminUserProfileFormHandler;
import org.wahlzeit.handlers.ChangePasswordFormHandler;
import org.wahlzeit.handlers.ConfirmAccountPageHandler;
import org.wahlzeit.handlers.EditPhotoCaseFormHandler;
import org.wahlzeit.handlers.EditUserPhotoFormHandler;
import org.wahlzeit.handlers.EditUserProfileFormHandler;
import org.wahlzeit.handlers.EmailPasswordFormHandler;
import org.wahlzeit.handlers.EmailUserNameFormHandler;
import org.wahlzeit.handlers.FilterPhotosFormHandler;
import org.wahlzeit.handlers.FilterPhotosPageHandler;
import org.wahlzeit.handlers.FlagPhotoFormHandler;
import org.wahlzeit.handlers.LoginFormHandler;
import org.wahlzeit.handlers.LogoutPageHandler;
import org.wahlzeit.handlers.NullFormHandler;
import org.wahlzeit.handlers.PartUtil;
import org.wahlzeit.handlers.PraisePhotoFormHandler;
import org.wahlzeit.handlers.ResetSessionPageHandler;
import org.wahlzeit.handlers.SendEmailFormHandler;
import org.wahlzeit.handlers.SetLanguagePageHandler;
import org.wahlzeit.handlers.SetOptionsFormHandler;
import org.wahlzeit.handlers.SetPhotoSizePageHandler;
import org.wahlzeit.handlers.ShowAdminPageHandler;
import org.wahlzeit.handlers.ShowInfoPageHandler;
import org.wahlzeit.handlers.ShowNotePageHandler;
import org.wahlzeit.handlers.ShowPartPageHandler;
import org.wahlzeit.handlers.ShowPhotoCasesPageHandler;
import org.wahlzeit.handlers.ShowPhotoPageHandler;
import org.wahlzeit.handlers.ShowUserHomePageHandler;
import org.wahlzeit.handlers.ShowUserPhotoFormHandler;
import org.wahlzeit.handlers.ShowUserProfileFormHandler;
import org.wahlzeit.handlers.SignupFormHandler;
import org.wahlzeit.handlers.TellFriendFormHandler;
import org.wahlzeit.handlers.UploadPhotoFormHandler;
import org.wahlzeit.handlers.WebPartHandler;
import org.wahlzeit.handlers.WebPartHandlerManager;
import org.wahlzeit.model.AccessRights;

import com.google.inject.Injector;

/**
 * A Main class that runs a Wahlzeit web server.
 * 
 * @author dirkriehle
 *
 */
public class ServerMain extends AbstractMain {
	
	@Inject
	protected Injector injector;
	
	@Inject
	protected WebPartHandlerManager webPartHandlerManager;

	/**
	 * 
	 */
	@Override
	public synchronized void requestStop() {
		sysLog.logInfo("setting stop signal for http server");
		super.requestStop();
	}
	
	/**
	 * 
	 */
	protected HttpServer httpServer = null;
	
	/**
	 * 
	 */
	protected void startUp() throws Exception {
		super.startUp();

		httpServer = createHttpServer();
		configureHttpServer(httpServer);
		
		configurePartHandlers();
			
		startHttpServer(httpServer);
	}
	
	/**
	 * 
	 */
	protected void execute() throws Exception {
		wait(); // really, any condition is fine
	}

	/**
	 * 
	 */
	protected void shutDown() throws Exception {
		if (httpServer != null) {
			stopHttpServer(httpServer);
		}
		
		super.shutDown();
	}
	
	/**
	 * 
	 */
	protected HttpServer createHttpServer() throws IOException {
		HttpServer server = new HttpServer();

		SocketListener listener = new SocketListener();
		listener.setPort(sysConfig.getHttpPortAsInt());
		server.addListener(listener);

		return server;
	}
	
	/**
	 * 
	 */
	protected void configureHttpServer(HttpServer server) {

		// Favicon hack
		
		HttpContext faviconContext = new HttpContext();
		faviconContext.setContextPath("/favicon.ico");
		server.addContext(faviconContext);

		ResourceHandler faviconHandler = new ResourceHandler();
		faviconContext.setResourceBase(sysConfig.getStaticDir().getRootPath());
		faviconContext.addHandler(faviconHandler);

		faviconContext.addHandler(new NotFoundHandler());

		// robots.txt hack
		
		HttpContext robotsContext = new HttpContext();
		robotsContext.setContextPath("/robots.txt");
		server.addContext(robotsContext);

		ResourceHandler robotsHandler = new ResourceHandler();
		robotsContext.setResourceBase(sysConfig.getStaticDir().getRootPath());
		robotsContext.addHandler(robotsHandler);

		robotsContext.addHandler(new NotFoundHandler());
		
		// Dynamic content
		
		HttpContext servletContext = new HttpContext();
		servletContext.setContextPath("/");
		server.addContext(servletContext);
		
		ServletHandler servlets = new ServletHandler();
		
		servletContext.addHandler(servlets);
		MainServletHack.setInjector(injector);
		servlets.addServlet("/*", MainServletHack.class.getCanonicalName());

		servletContext.addHandler(new NotFoundHandler());

		// Photos content
		
		HttpContext photosContext = new HttpContext();
		photosContext.setContextPath(sysConfig.getPhotosUrlPathAsString());
		server.addContext(photosContext);

		ResourceHandler photosHandler = new ResourceHandler();
		photosContext.setResourceBase(sysConfig.getPhotosDirAsString());
		photosContext.addHandler(photosHandler);

		photosContext.addHandler(new NotFoundHandler());

		// Static content
		
		HttpContext staticContext = new HttpContext();
		staticContext.setContextPath(sysConfig.getStaticDir().getRootUrl());
		server.addContext(staticContext);

		ResourceHandler staticHandler = new ResourceHandler();
		staticContext.setResourceBase(sysConfig.getStaticDir().getRootPath());
		staticContext.addHandler(staticHandler);

		// Not Found
		staticContext.addHandler(new NotFoundHandler());
	}

	/**
	 * 
	 */
	protected void startHttpServer(HttpServer httpServer) throws Exception {
		httpServer.start();
		sysLog.logInfo("http server was started");
	}
	
	/**
	 * 
	 */
	protected void stopHttpServer(HttpServer httpServer) {
		try {
			httpServer.stop(true);
		} catch (InterruptedException ie) {
			sysLog.logThrowable(ie);
		}
		
		sysLog.logInfo("http server was stopped");
	}
	
	/**
	 * 
	 */
	protected void configurePartHandlers() {
		WebPartHandler temp = null;
		ShowInfoPageHandler.Factory showInfoPageHandlerFactory = injector.getInstance(ShowInfoPageHandler.Factory.class);
		ShowPartPageHandler.Factory showPartPageHandlerFactory = injector.getInstance(ShowPartPageHandler.Factory.class);

		
		// NullInfo and NullForm
		webPartHandlerManager.addWebPartHandler(PartUtil.NULL_FORM_NAME, injector.getInstance(NullFormHandler.class));
		
		// Note page
		webPartHandlerManager.addWebPartHandler(PartUtil.SHOW_NOTE_PAGE_NAME, injector.getInstance(ShowNotePageHandler.class));

		// ShowPhoto page
		webPartHandlerManager.addWebPartHandler(PartUtil.FILTER_PHOTOS_FORM_NAME, injector.getInstance(FilterPhotosFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.PRAISE_PHOTO_FORM_NAME, injector.getInstance(PraisePhotoFormHandler.class));

		temp = injector.getInstance(ShowPhotoPageHandler.class);
		webPartHandlerManager.addWebPartHandler(PartUtil.SHOW_PHOTO_PAGE_NAME, temp);
		webPartHandlerManager.addWebPartHandler(PartUtil.ENGAGE_GUEST_FORM_NAME, temp);
		
		webPartHandlerManager.addWebPartHandler(PartUtil.FILTER_PHOTOS_PAGE_NAME, injector.getInstance(FilterPhotosPageHandler.class));

		webPartHandlerManager.addWebPartHandler(PartUtil.RESET_SESSION_PAGE_NAME, injector.getInstance(ResetSessionPageHandler.class));
		
		// About and Terms pages
		webPartHandlerManager.addWebPartHandler(PartUtil.ABOUT_PAGE_NAME, showInfoPageHandlerFactory.create(AccessRights.GUEST, PartUtil.ABOUT_INFO_FILE));
		webPartHandlerManager.addWebPartHandler(PartUtil.CONTACT_PAGE_NAME, showInfoPageHandlerFactory.create(AccessRights.GUEST, PartUtil.CONTACT_INFO_FILE));
		webPartHandlerManager.addWebPartHandler(PartUtil.IMPRINT_PAGE_NAME, showInfoPageHandlerFactory.create(AccessRights.GUEST, PartUtil.IMPRINT_INFO_FILE));
		webPartHandlerManager.addWebPartHandler(PartUtil.TERMS_PAGE_NAME, showInfoPageHandlerFactory.create(AccessRights.GUEST, PartUtil.TERMS_INFO_FILE));

		// Flag, Send, Tell, and Options pages
		temp = webPartHandlerManager.addWebPartHandler(PartUtil.FLAG_PHOTO_FORM_NAME, injector.getInstance(FlagPhotoFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.FLAG_PHOTO_PAGE_NAME, showPartPageHandlerFactory.create(AccessRights.GUEST, temp));
		temp = webPartHandlerManager.addWebPartHandler(PartUtil.SEND_EMAIL_FORM_NAME, injector.getInstance(SendEmailFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.SEND_EMAIL_PAGE_NAME, showPartPageHandlerFactory.create(AccessRights.GUEST, temp));
		temp = webPartHandlerManager.addWebPartHandler(PartUtil.TELL_FRIEND_FORM_NAME, injector.getInstance(TellFriendFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.TELL_FRIEND_PAGE_NAME, showPartPageHandlerFactory.create(AccessRights.GUEST, temp));
		temp = webPartHandlerManager.addWebPartHandler(PartUtil.SET_OPTIONS_FORM_NAME, injector.getInstance(SetOptionsFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.SET_OPTIONS_PAGE_NAME, showPartPageHandlerFactory.create(AccessRights.GUEST, temp));
		
		// Signup, Login, EmailUserName/Password, and Logout pages
		temp = webPartHandlerManager.addWebPartHandler(PartUtil.SIGNUP_FORM_NAME, injector.getInstance(SignupFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.SIGNUP_PAGE_NAME, showPartPageHandlerFactory.create(AccessRights.GUEST, temp));

		webPartHandlerManager.addWebPartHandler(PartUtil.CONFIRM_ACCOUNT_PAGE_NAME, injector.getInstance(ConfirmAccountPageHandler.class));

		temp = webPartHandlerManager.addWebPartHandler(PartUtil.LOGIN_FORM_NAME, injector.getInstance(LoginFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.LOGIN_PAGE_NAME, showPartPageHandlerFactory.create(AccessRights.GUEST, temp));
		temp = webPartHandlerManager.addWebPartHandler(PartUtil.EMAIL_USER_NAME_FORM_NAME, injector.getInstance(EmailUserNameFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.EMAIL_USER_NAME_PAGE_NAME, showPartPageHandlerFactory.create(AccessRights.GUEST, temp));
		temp = webPartHandlerManager.addWebPartHandler(PartUtil.EMAIL_PASSWORD_FORM_NAME, injector.getInstance(EmailPasswordFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.EMAIL_PASSWORD_PAGE_NAME, showPartPageHandlerFactory.create(AccessRights.GUEST, temp));

		webPartHandlerManager.addWebPartHandler(PartUtil.LOGOUT_PAGE_NAME, injector.getInstance(LogoutPageHandler.class));
		
		// SetLanguage pages
		temp = injector.getInstance(SetLanguagePageHandler.class);
		webPartHandlerManager.addWebPartHandler(PartUtil.SET_ENGLISH_LANGUAGE_PAGE_NAME, temp);
		webPartHandlerManager.addWebPartHandler(PartUtil.SET_GERMAN_LANGUAGE_PAGE_NAME, temp);
		webPartHandlerManager.addWebPartHandler(PartUtil.SET_SPANISH_LANGUAGE_PAGE_NAME, temp);
		webPartHandlerManager.addWebPartHandler(PartUtil.SET_JAPANESE_LANGUAGE_PAGE_NAME, temp);

		// SetPhotoSize pages
		temp = injector.getInstance(SetPhotoSizePageHandler.class);
		webPartHandlerManager.addWebPartHandler(PartUtil.SET_EXTRA_SMALL_PHOTO_SIZE_PAGE_NAME, temp);
		webPartHandlerManager.addWebPartHandler(PartUtil.SET_SMALL_PHOTO_SIZE_PAGE_NAME, temp);
		webPartHandlerManager.addWebPartHandler(PartUtil.SET_MEDIUM_PHOTO_SIZE_PAGE_NAME, temp);
		webPartHandlerManager.addWebPartHandler(PartUtil.SET_LARGE_PHOTO_SIZE_PAGE_NAME, temp);
		webPartHandlerManager.addWebPartHandler(PartUtil.SET_EXTRA_LARGE_PHOTO_SIZE_PAGE_NAME, temp);

		// ShowHome page
		webPartHandlerManager.addWebPartHandler(PartUtil.SHOW_USER_PROFILE_FORM_NAME, injector.getInstance(ShowUserProfileFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.SHOW_USER_PHOTO_FORM_NAME, injector.getInstance(ShowUserPhotoFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.SHOW_USER_HOME_PAGE_NAME, injector.getInstance(ShowUserHomePageHandler.class));
		
		// EditProfile, ChangePassword, EditPhoto, and UploadPhoto pages
		temp = webPartHandlerManager.addWebPartHandler(PartUtil.EDIT_USER_PROFILE_FORM_NAME, injector.getInstance(EditUserProfileFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.EDIT_USER_PROFILE_PAGE_NAME, showPartPageHandlerFactory.create(AccessRights.USER, temp));
		temp = webPartHandlerManager.addWebPartHandler(PartUtil.CHANGE_PASSWORD_FORM_NAME, injector.getInstance(ChangePasswordFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.CHANGE_PASSWORD_PAGE_NAME, showPartPageHandlerFactory.create(AccessRights.USER, temp));
		temp = webPartHandlerManager.addWebPartHandler(PartUtil.EDIT_USER_PHOTO_FORM_NAME, injector.getInstance(EditUserPhotoFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.EDIT_USER_PHOTO_PAGE_NAME, showPartPageHandlerFactory.create(AccessRights.USER, temp));
		temp = webPartHandlerManager.addWebPartHandler(PartUtil.UPLOAD_PHOTO_FORM_NAME, injector.getInstance(UploadPhotoFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.UPLOAD_PHOTO_PAGE_NAME, showPartPageHandlerFactory.create(AccessRights.USER, temp));
		
		webPartHandlerManager.addWebPartHandler(PartUtil.EDIT_PHOTO_CASE_FORM_NAME, injector.getInstance(EditPhotoCaseFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.SHOW_PHOTO_CASES_PAGE_NAME, injector.getInstance(ShowPhotoCasesPageHandler.class));

		// Admin page incl. AdminUserProfile and AdminUserPhoto
		temp = injector.getInstance(ShowAdminPageHandler.class);
		webPartHandlerManager.addWebPartHandler(PartUtil.SHOW_ADMIN_PAGE_NAME, temp);
		webPartHandlerManager.addWebPartHandler(PartUtil.SHOW_ADMIN_MENU_FORM_NAME, temp);
		webPartHandlerManager.addWebPartHandler(PartUtil.ADMIN_USER_PROFILE_FORM_NAME, injector.getInstance(AdminUserProfileFormHandler.class));
		webPartHandlerManager.addWebPartHandler(PartUtil.ADMIN_USER_PHOTO_FORM_NAME, injector.getInstance(AdminUserPhotoFormHandler.class));
	}
}
