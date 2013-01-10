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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public abstract class AbstractPersistent implements Persistent {

	/**
	 * 
	 */
	protected transient int writeCount = 0;

	/**
	 * 
	 */
	public boolean isDirty() {
		return writeCount != 0;
	}

	/**
	 * 
	 */
	public final void incWriteCount() {
		writeCount++;
	}

	/**
	 * 
	 */
	public void resetWriteCount() {
		writeCount = 0;
	}

	/**
	 * 
	 */
	public void readFrom(ResultSet rset) throws SQLException {
		Class<?> clazz = getClass();
		while(clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			for(Field field: fields) {
				Persist persist = field.getAnnotation(Persist.class);
				if(persist != null) {
					readFieldFrom(rset, field, persist);
				}
			}
			
			clazz = clazz.getSuperclass();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void readFieldFrom(ResultSet rset, Field field, Persist persist) throws SQLException {
		try {
			Persistor<Object, ?> persistor;
			try {
				persistor = (Persistor<Object, ?>) persist.persistor().newInstance();
			} catch(InstantiationException e) {
				persistor = (Persistor<Object, ?>) persist.persistor().getConstructor(Class.class).newInstance(field.getType());
			}
			String column = persist.column();
			if(column.equals("")) {
				column = convertCamelCaseToUnderlines(field.getName());
			}
			Object value = persistor.readFrom(this, rset, column);
			field.setAccessible(true);
			field.set(this, value);
		} catch(IllegalAccessException | InstantiationException | 
				IllegalArgumentException | InvocationTargetException | 
				NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			// and ignore
		}
	}

	/**
	 * 
	 */
	public void writeOn(ResultSet rset) throws SQLException {
		Class<?> clazz = getClass();
		while(clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			for(Field field: fields) {
				Persist persist = field.getAnnotation(Persist.class);
				if(persist != null) {
					writeFieldOn(rset, field, persist);
				}
			}
			
			clazz = clazz.getSuperclass();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void writeFieldOn(ResultSet rset, Field field, Persist persist) throws SQLException {
		try {
			Persistor<Object, Object> persistor;
			try {
				persistor = (Persistor<Object, Object>) persist.persistor().newInstance();
			} catch(InstantiationException e) {
				persistor = (Persistor<Object, Object>) persist.persistor().getConstructor(Class.class).newInstance(field.getType());
			}
			String column = persist.column();
			if(column.equals("")) {
				column = convertCamelCaseToUnderlines(field.getName());
			}
			field.setAccessible(true);
			persistor.writeOn(this, rset, column, field.get(this));
		} catch(IllegalAccessException | InstantiationException | 
				IllegalArgumentException | InvocationTargetException | 
				NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			// and ignore
		}
	}

	/**
	 * 
	 */
	public void writeId(PreparedStatement stmt, int pos) throws SQLException {
		throw new RuntimeException("Missing writeId implementation in " + getClass().getCanonicalName());
	}
	
	/**
	 * 
	 */
	private String convertCamelCaseToUnderlines(String name) {
		StringBuilder sb = new StringBuilder();
		for(char c: name.toCharArray()) {
			if(Character.isUpperCase(c) && sb.length() != 0) {
				sb.append("_");
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

}