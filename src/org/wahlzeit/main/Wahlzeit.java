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

import org.wahlzeit.agents.AgentsModule;
import org.wahlzeit.handlers.HandlersModule;
import org.wahlzeit.model.EnglishModelConfig;
import org.wahlzeit.model.GermanModelConfig;
import org.wahlzeit.model.GlobalsPersistance;
import org.wahlzeit.model.ModelConfig;
import org.wahlzeit.model.ModelModule;
import org.wahlzeit.model.Saveable;
import org.wahlzeit.services.Language;
import org.wahlzeit.webparts.WebPartsModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * 
 * @author dirkriehle
 *
 */
public class Wahlzeit extends MainModule {
	
	public Wahlzeit(boolean production) {
		super(production);
	}

	/**
	 * 
	 */
	public static void main(String[] argv) {
		boolean isInProductionFlag = false;
		
		for (int i = 0; i < argv.length; i++) {
			String arg = argv[i];
			if (arg.equals("-P") || arg.equals("--production")) {
				isInProductionFlag = true;
			} else if (arg.equals("-D") || arg.equals("--development")) {
				isInProductionFlag = false;
			}
		}		

		Injector injector = Guice.createInjector(new Wahlzeit(isInProductionFlag));
		
		injector.getInstance(Main.class).run(); 
	}

	@Override
	protected void configure() {
		super.configure();

		install(new ModelModule());
		install(new AgentsModule());
		install(new WebPartsModule());
		install(new HandlersModule());

		bind(ServerMain.class).in(Scopes.SINGLETON);
		bind(Main.class).to(ServerMain.class);
		bind(Boolean.class).annotatedWith(Names.named("production")).toInstance(production);
		if(production) {
			bind(String.class).annotatedWith(Names.named("host")).toInstance("flowers.wahlzeit.com");
			bind(String.class).annotatedWith(Names.named("port")).toInstance("80");
		} else {
			bind(String.class).annotatedWith(Names.named("host")).toInstance("localhost");
			bind(String.class).annotatedWith(Names.named("port")).toInstance("8585");
		}
		
		Multibinder.newSetBinder(binder(), Saveable.class).addBinding().to(GlobalsPersistance.class);
		
		MapBinder<Language, ModelConfig> languageModelBinder = MapBinder.newMapBinder(binder(), Language.class, ModelConfig.class);
		languageModelBinder.addBinding(Language.ENGLISH).to(EnglishModelConfig.class).in(Scopes.SINGLETON);
		languageModelBinder.addBinding(Language.GERMAN).to(GermanModelConfig.class).in(Scopes.SINGLETON);
	}

}
