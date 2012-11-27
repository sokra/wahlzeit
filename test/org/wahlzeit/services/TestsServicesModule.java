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

package org.wahlzeit.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.wahlzeit.model.UserSession;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class TestsServicesModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(EmailServer.class).to(NullEmailServer.class).in(Scopes.SINGLETON);
		bind(Session.class).to(NullSession.class);
		bind(UserSession.class).to(NullUserSession.class);
		
		bind(DateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS"));

		bind(String.class).annotatedWith(Names.named("host")).toInstance("localhost");
		bind(String.class).annotatedWith(Names.named("port")).toInstance("8585");
	}

}
