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

import org.wahlzeit.model.ModelConfig;
import org.wahlzeit.model.Saveable;
import org.wahlzeit.services.Language;
import org.wahlzeit.services.ServicesModule;
import org.wahlzeit.utils.Lifecycle;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * 
 * @author dirkriehle
 *
 */
public class MainModule extends AbstractModule {
	
	protected final boolean production;

	public MainModule(boolean production) {
		this.production = production;
	}

	@Override
	protected void configure() {
		install(new ServicesModule(production));
		
		bind(Boolean.class).annotatedWith(Names.named("production")).toInstance(production);
		
		MapBinder.newMapBinder(binder(), Language.class, ModelConfig.class);
		Multibinder.newSetBinder(binder(), Saveable.class);
		Multibinder.newSetBinder(binder(), Lifecycle.class);
	}

}
