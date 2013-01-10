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

package org.wahlzeit.services.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultPersistor implements Persistor<Object, Object> {
	
	private Class<? extends Persistent> clazz;

	public DefaultPersistor(Class<? extends Persistent> clazz) {
		this.clazz = clazz;
	}

	@Override
	public Object readFrom(Object context, ResultSet rset, String column)
			throws SQLException {
		if(clazz.equals(Integer.class) || clazz.equals(Integer.TYPE)) {
			return rset.getInt(column);
		} else if(clazz.equals(String.class)) {
			return rset.getString(column);
		} else if(clazz.equals(Long.class) || clazz.equals(Long.TYPE)) {
			return rset.getLong(column);
		} else if(clazz.equals(Boolean.class) || clazz.equals(Boolean.TYPE)) {
			return rset.getBoolean(column);
		} else {
			throw new IllegalArgumentException(clazz.getCanonicalName() + " cannot be persisted by the default persistor.");
		}
	}

	@Override
	public void writeOn(Object context, ResultSet rset, String column,
			Object value) throws SQLException {
		if(clazz.equals(Integer.class) || clazz.equals(Integer.TYPE)) {
			rset.updateInt(column, (int) value);
		} else if(clazz.equals(String.class)) {
			rset.updateString(column, (String) value);
		} else if(clazz.equals(Long.class) || clazz.equals(Long.TYPE)) {
			rset.updateLong(column, (long) value);
		} else if(clazz.equals(Boolean.class) || clazz.equals(Boolean.TYPE)) {
			rset.updateBoolean(column, (boolean) value);
		} else {
			throw new IllegalArgumentException(clazz.getCanonicalName() + " cannot be persisted by the default persistor.");
		}
	}


}
