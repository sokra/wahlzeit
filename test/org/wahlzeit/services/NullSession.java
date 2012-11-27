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


/**
 * The NullSession does nothing.
 * 
 */
public class NullSession implements Session {

	@Override
	public String getName() {
		return "null session";
	}

	@Override
	public boolean hasDatabaseConnection() {
		return false;
	}

	@Override
	public DatabaseConnection getDatabaseConnection() {
		return null;
	}

	@Override
	public void dropDatabaseConnection() {
	}

	@Override
	public String getClientName() {
		return null;
	}

	@Override
	public void resetProcessingTime() {
	}

	@Override
	public void addProcessingTime(long time) {
	}

	@Override
	public long getProcessingTime() {
		return -1;
	}
}
