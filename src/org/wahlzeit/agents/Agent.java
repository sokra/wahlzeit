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

package org.wahlzeit.agents;

import javax.inject.Inject;

import org.wahlzeit.services.ContextProvider;
import org.wahlzeit.services.Session;
import org.wahlzeit.services.SysLog;
import org.wahlzeit.services.SysSession;

import com.google.inject.Injector;

/**
 * 
 * @author dirkriehle
 *
 */
public abstract class Agent implements Runnable {
	
	@Inject
	protected Injector injector;
	
	@Inject
	protected SysLog sysLog;
	
	@Inject
	protected ContextProvider contextProvider;
	
	@Inject
	protected SysSession.Factory sysSessionFactory;
	
	/**
	 * 
	 */
	protected static int id = 0;
	
	/**
	 * 
	 */
	protected String name = "no name";
	
	/**
	 * 
	 */
	protected boolean isToStop = false;
	
	/**
	 * Full period after which task gets repeated
	 */
	protected long period = 60 * 60 * 1000; // default = 1h, in millis
	
	/**
	 * 
	 */
	protected Agent() {
		// do nothing
	}
	
	/**
	 *@methodtype initialization
	 */
	protected void initialize(String myName, long myPeriod) {
		name = myName;
		period = myPeriod;
	}
	
	/**
	 * @methodtype get
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @methodtype command
	 */
	public void run() {
		synchronized(Agent.class) {
			Session ctx = sysSessionFactory.create("agent" + id++);
			contextProvider.set(ctx);
		}

		while(!isToStop) {
			try {
				sysLog.logInfo("going to sleep for: " + (period / 1000) + " seconds");
				Thread.sleep(period);
			} catch (Exception ex) {
				// do nothing
			}
			sysLog.logInfo("just woke up");
			doRun();
		}
	}
	
	/**
	 * 
	 */
	protected void doRun() {
		// do nothing
	}
	
	/**
	 * 
	 */
	public long getPeriod() {
		return period;
	}
	
	
	/**
	 *
	 */
	public void stop() {
		isToStop = true;
	}
	
}
