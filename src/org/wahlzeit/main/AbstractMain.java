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

import java.util.Set;

import javax.inject.Inject;

import org.wahlzeit.model.Saveable;
import org.wahlzeit.services.AbstractSession;
import org.wahlzeit.services.ContextProvider;
import org.wahlzeit.services.SysConfig;
import org.wahlzeit.services.SysLog;
import org.wahlzeit.services.SysSession;
import org.wahlzeit.utils.Lifecycle;

import com.google.inject.Injector;

/**
 * 
 * @author dirkriehle
 *
 */
public abstract class AbstractMain implements Main {
	
	@Inject
	protected SysLog sysLog;
	
	@Inject
	protected SysConfig sysConfig;
	
	@Inject
	protected ContextProvider contextProvider;
	
	@Inject
	private Injector injector;
	
	@Inject
	protected Set<Lifecycle> lifecycleObjects;
	
	@Inject
	protected Set<Saveable> saveables;
	
	@Inject
	protected SysSession.Factory sysSessionFactory;
	
	/**
	 * 
	 */
	protected boolean isToStopFlag = false;
	
	/**
	 * 
	 */
	@Override
	public synchronized void requestStop() {
		isToStopFlag = true;
		this.notify();
	}
	
	/**
	 * 
	 */
	@Override
	public boolean isShuttingDown() {
		return isToStopFlag;
	}
	
	/**
	 * 
	 */
	@Override
	public synchronized void run() {
		try {
			startUp();
			execute();
		} catch(Exception ex) {
			sysLog.logThrowable(ex);
		}

		try {
			shutDown();
		} catch (Exception ex) {
			sysLog.logThrowable(ex);
		}

		for(Saveable saveable: saveables) {
			saveable.save();			
		}
	} 

	/**
	 * 
	 */
	protected void startUp() throws Exception {
		AbstractSession ctx = sysSessionFactory.create("system");
		contextProvider.set(ctx);
		for(Lifecycle obj: lifecycleObjects)
			obj.startUp();
	}
	
	/**
	 * 
	 */
	protected void execute() throws Exception {
		// do nothing
	}

	/**
	 * 
	 */
	protected void shutDown() throws Exception {
		for(Lifecycle obj: lifecycleObjects)
			obj.shutDown();
	}
	
}
