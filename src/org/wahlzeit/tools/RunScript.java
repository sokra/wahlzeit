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

import java.sql.Connection;
import java.sql.Statement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.wahlzeit.main.AbstractMain;
import org.wahlzeit.main.Main;
import org.wahlzeit.main.MainModule;
import org.wahlzeit.services.ConfigDir;
import org.wahlzeit.services.DatabaseConnection;
import org.wahlzeit.services.FileUtil;
import org.wahlzeit.services.Session;
import org.wahlzeit.services.SysConfig;
import org.wahlzeit.services.SysLog;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/**
 * 
 * @author dirkriehle
 *
 */
public class RunScript extends MainModule {

	/**
	 * 
	 */
	public static void main(String[] argv) {
		boolean isToRunScript = false;
		String scriptFileName = "dummy";

		for (int i = 0; i < argv.length; i++) {
			String arg = argv[i];
			if (arg.equals("-S") || arg.equals("--setup")) {
				isToRunScript = true;
				scriptFileName = "CreateTables.sql";
			} else if (arg.equals("-T") || arg.equals("--teardown")) {
				isToRunScript = true;
				scriptFileName = "DropTables.sql";
			} else if (arg.equals("--script") && (i++ < argv.length)) {
				isToRunScript = true;
				scriptFileName = argv[i];
			}
		}

		Injector injector = Guice.createInjector(new RunScript(isToRunScript, scriptFileName));
		
		RunScriptMain main = injector.getInstance(RunScriptMain.class);
		main.run();
	}
	
	public RunScript(boolean isToRunScript, String scriptFileName) {
		super(false);
		this.isToRunScript = isToRunScript;
		this.scriptFileName = scriptFileName;
	}

	/**
	 * 
	 */
	protected boolean isToRunScript;
	protected String scriptFileName;
	
	@Override
	protected void configure() {
		super.configure();

		bind(Main.class).to(RunScriptMain.class).in(Scopes.SINGLETON);
		
		bind(String.class).annotatedWith(Names.named("scriptFileName")).toInstance(scriptFileName);
	}
	
	static class RunScriptMain extends AbstractMain {
		
		@Inject
		protected Provider<Session> contextProvider;
		
		@Inject
		protected SysConfig sysConfig;
		
		@Inject
		protected SysLog sysLog;
		
		@Inject
		protected FileUtil fileUtil;
		
		@Inject @Named("scriptFileName")
		protected String scriptFileName;
	
		/**
		 * 
		 */
		protected void execute() throws Exception {
			DatabaseConnection dbc = contextProvider.get().getDatabaseConnection();
			Connection conn = dbc.getRdbmsConnection();
			
			ConfigDir scriptsDir = sysConfig.getScriptsDir();
			String defaultScriptFileName = scriptsDir.getDefaultConfigFileName(scriptFileName);
			runScript(conn, defaultScriptFileName);
				
			if(scriptsDir.hasCustomFile("CreateTables.sql")) {
				String customConfigFileName = scriptsDir.getCustomConfigFileName(scriptFileName);
				runScript(conn, customConfigFileName);
			}
		}
	
		/**
		 * 
		 */
		protected void runScript(Connection conn, String fullFileName) throws Exception {
			String query = fileUtil.safelyReadFileAsString(fullFileName);
			sysLog.logQuery(query);
	
			Statement stmt = conn.createStatement();
			stmt.execute(query);
		}
	}
	
}
