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

package org.wahlzeit.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.wahlzeit.TestsModule;
import org.wahlzeit.model.EnglishModelConfig;
import org.wahlzeit.model.ModelConfig;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.services.EmailAddress;
import org.wahlzeit.services.EmailServer;
import org.wahlzeit.services.MockEmailServer;
import org.wahlzeit.services.MockUserSession;
import org.wahlzeit.webparts.WebPart;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

/**
 * Acceptance tests for the TellFriend feature.
 * 
 * @author dirkriehle
 *
 */
public class TellFriendTest extends TestCase {
	
	/**
	 * 
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(TellFriendTest.class);
	}

	/**
	 * 
	 */
	public TellFriendTest(String name) {
		super(name);
	}
	
	/**
	 * 
	 */
	public void testTellFriendMakeWebPart() {
		Injector injector = Guice.createInjector(Modules.override(new TestsModule()).with(new AbstractModule() {
			
			@Override
			protected void configure() {
				bind(UserSession.class).to(MockUserSession.class);
				bind(ModelConfig.class).to(EnglishModelConfig.class);
			}

		}));
		
		TellFriendFormHandler handler = injector.getInstance(TellFriendFormHandler.class);
		UserSession session = injector.getInstance(UserSession.class);
		
		WebPart part = handler.makeWebPart(session);
		// no failure is good behavior
		
		EmailAddress to = EmailAddress.getFromString("engel@himmel.de");
		Map<String, String> args = new HashMap<String, String>();
		args.put(TellFriendFormHandler.EMAIL_TO, to.asString());
		args.put(TellFriendFormHandler.EMAIL_SUBJECT, "Oh well...");
		handler.handlePost(session, args);
		
		part = handler.makeWebPart(session);
		assertEquals(to.asString(), part.getValue(TellFriendFormHandler.EMAIL_TO));
		assertEquals("Oh well...", part.getValue(TellFriendFormHandler.EMAIL_SUBJECT));
	}

	/**
	 * 
	 */
	public void testTellFriendPost() {
		Injector injector = Guice.createInjector(Modules.override(new TestsModule()).with(new AbstractModule() {
			
			@Override
			protected void configure() {
				bind(UserSession.class).to(MockUserSession.class);
				bind(ModelConfig.class).to(EnglishModelConfig.class);
				bind(MockEmailServer.class).in(Scopes.SINGLETON);
				bind(EmailServer.class).to(MockEmailServer.class);
			}

		}));
		
		TellFriendFormHandler handler = injector.getInstance(TellFriendFormHandler.class);
		UserSession session = injector.getInstance(UserSession.class);
		MockEmailServer mockServer = injector.getInstance(MockEmailServer.class);

		EmailAddress from = EmailAddress.getFromString("info@wahlzeit.org");
		EmailAddress to = EmailAddress.getFromString("fan@yahoo.com");
		EmailAddress bcc = session.cfg().getAuditEmailAddress();
		String subject = "Coolest website ever!";
		String body = "You've got to check this out!";

		Map<String, String> args = new HashMap<String, String>();
		args.put(TellFriendFormHandler.EMAIL_FROM, from.asString());
		args.put(TellFriendFormHandler.EMAIL_TO, to.asString());
		args.put(TellFriendFormHandler.EMAIL_SUBJECT, subject);
		args.put(TellFriendFormHandler.EMAIL_BODY, body);
		
		mockServer.expect(from, to, bcc, subject, body);
		handler.handlePost(session, args);
		mockServer.assertCalled();
		
		handler.handlePost(session, Collections.<String, Object>emptyMap()); // will fail if email is sent
	}

}
