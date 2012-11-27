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

import org.wahlzeit.utils.Lifecycle;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

public class ModelModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(PhotoCaseManager.class).in(Scopes.SINGLETON);
		bind(PhotoManager.class).in(Scopes.SINGLETON);
		bind(UserManager.class).in(Scopes.SINGLETON);
		bind(PhotoFactory.class).in(Scopes.SINGLETON);
		
		Multibinder<Lifecycle> lifecycleBinder = Multibinder.newSetBinder(binder(), Lifecycle.class);
		lifecycleBinder.addBinding().to(PhotoCaseManager.class);
		lifecycleBinder.addBinding().to(GlobalsPersistance.class);

		Multibinder<Saveable> saveableBinder = Multibinder.newSetBinder(binder(), Saveable.class);
		saveableBinder.addBinding().to(PhotoCaseManager.class);
		saveableBinder.addBinding().to(PhotoManager.class);
		saveableBinder.addBinding().to(UserManager.class);
		saveableBinder.addBinding().to(GlobalsPersistance.class);
	}

}
