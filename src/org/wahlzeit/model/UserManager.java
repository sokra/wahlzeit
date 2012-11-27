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

import java.util.Collection;

import org.wahlzeit.services.EmailAddress;

public interface UserManager {

	/**
	 * 
	 */
	boolean hasUserByName(String name);

	/**
	 * 
	 */
	boolean hasUserByTag(String tag);

	/**
	 * 
	 */
	User getUserByName(String name);

	/**
	 * 
	 */
	User getUserByTag(String tag);

	/**
	 * 
	 */
	void addUser(User user);

	/**
	 * 
	 */
	void removeUser(User user);

	/**
	 * 
	 */
	void loadUsers(Collection<User> result);

	/**
	 * 
	 */
	long createConfirmationCode();

	/**
	 * 
	 */
	void emailWelcomeMessage(UserSession ctx, User user);

	/**
	 * 
	 */
	void emailConfirmationRequest(UserSession ctx, User user);

	/**
	 * 
	 */
	void saveUser(User user);

	/**
	 * 
	 */
	void dropUser(User user);

	/**
	 * 
	 */
	void saveUsers();

	/**
	 * 
	 */
	User getUserByEmailAddress(String emailAddress);

	/**
	 * 
	 */
	User getUserByEmailAddress(EmailAddress emailAddress);

}