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

import java.util.*;
import java.sql.*;

import org.wahlzeit.services.*;
import org.wahlzeit.services.mailing.*;

/**
 * The UserManager provides access to and manages Users (including Moderators and Administrators).
 * 
 * @author dirkriehle
 *
 */
public class UserManager extends ObjectManager {

	/**
	 *
	 */
	protected static UserManager instance = new UserManager();

	/**
	 * 
	 */
	public static UserManager getInstance() {
		return instance;
	}
	
	/**
	 * Maps nameAsTag to user of that name (as tag)
	 */
	protected Map<String, Client> users = new HashMap<String, Client>();
	
	/**
	 * 
	 */
	protected Random codeGenerator = new Random(System.currentTimeMillis());

	/**
	 * 
	 */
	public boolean hasUserByName(String name) {
		assertIsNonNullArgument(name, "user-by-name");
		return hasUserByTag(Tags.asTag(name));
	}
	
	/**
	 * 
	 */
	public boolean hasUserByTag(String tag) {
		assertIsNonNullArgument(tag, "user-by-tag");
		return getUserByTag(tag) != null;
	}
	
	/**
	 * 
	 */
	protected boolean doHasUserByTag(String tag) {
		return doGetUserByTag(tag) != null;
	}
	
	/**
	 * 
	 */
	public Client getUserByName(String name) {
		return getUserByTag(Tags.asTag(name));
	}
	
	/**
	 * 
	 */
	public Client getUserByTag(String tag) {
		assertIsNonNullArgument(tag, "user-by-tag");

		Client result = doGetUserByTag(tag);

		if (result == null) {
			try {
				result = (Client) readObject(getReadingStatement("SELECT * FROM users WHERE name_as_tag = ?"), tag);
			} catch (SQLException sex) {
				SysLog.logThrowable(sex);
			}
			
			if (result != null) {
				doAddUser(result);
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 */
	protected Client doGetUserByTag(String tag) {
		return users.get(tag);
	}
	
	/**
	 * 
	 * @methodtype factory
	 */
	protected Client createObject(ResultSet rset) throws SQLException {
		Client result = new Client();

		result.readFrom(rset);
		return result;
	}
	
	/**
	 * 
	 */
	public void addUser(Client user) {
		assertIsNonNullArgument(user);
		assertIsUnknownUserAsIllegalArgument(user);
		assertHasUserRole(user);

		try {
			int id = user.getRole(UserRole.class).getId();
			createObject(user, getReadingStatement("INSERT INTO users(id) VALUES(?)"), id);
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		doAddUser(user);		
	}
	
	/**
	 * 
	 */
	protected void doAddUser(Client user) {
		users.put(user.getRole(UserRole.class).getNameAsTag(), user);
	}
	
	/**
	 * 
	 */
	public void deleteUser(Client user) {
		assertIsNonNullArgument(user);
		doDeleteUser(user);

		try {
			deleteObject(user, getReadingStatement("DELETE FROM users WHERE id = ?"));
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		assertIsUnknownUserAsIllegalState(user);
	}
	
	/**
	 * 
	 */
	protected void doDeleteUser(Client user) {
		users.remove(user.getRole(UserRole.class).getNameAsTag());
	}
	
	/**
	 * 
	 */
	public void loadUsers(Collection<Client> result) {
		try {
			readObjects(result, getReadingStatement("SELECT * FROM users"));
			for (Iterator<Client> i = result.iterator(); i.hasNext(); ) {
				Client user = i.next();
				if (!doHasUserByTag(user.getRole(UserRole.class).getNameAsTag())) {
					doAddUser(user);
				} else {
					SysLog.logValueWithInfo("user", user.getRole(UserRole.class).getName(), "user had already been loaded");
				}
			}
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		SysLog.logInfo("loaded all users");
	}
	
	/**
	 * 
	 */
	public long createConfirmationCode() {
		return Math.abs(codeGenerator.nextLong() / 2);
	}
	
	/**
	 * 
	 */
	public void emailWelcomeMessage(UserSession ctx, Client user) {
		assertHasUserRole(user);

		EmailAddress from = ctx.cfg().getAdministratorEmailAddress();
		EmailAddress to = user.getEmailAddress();

		String emailSubject = ctx.cfg().getWelcomeEmailSubject();
		String emailBody = ctx.cfg().getWelcomeEmailBody() + "\n\n";
		emailBody += ctx.cfg().getWelcomeEmailUserName() + user.getRole(UserRole.class).getName() + "\n\n"; 
		emailBody += ctx.cfg().getConfirmAccountEmailBody() + "\n\n";
		emailBody += SysConfig.getSiteUrlAsString() + "confirm?code=" + user.getRole(UserRole.class).getConfirmationCode() + "\n\n";
		emailBody += ctx.cfg().getGeneralEmailRegards() + "\n\n----\n";
		emailBody += ctx.cfg().getGeneralEmailFooter() + "\n\n";

		EmailService emailService = EmailServiceManager.getDefaultService();
		emailService.sendEmailIgnoreException(from, to, ctx.cfg().getAuditEmailAddress(), emailSubject, emailBody);
	}
	
	/**
	 * 
	 */
	public void emailConfirmationRequest(UserSession ctx, Client user) {
		assertHasUserRole(user);

		EmailAddress from = ctx.cfg().getAdministratorEmailAddress();
		EmailAddress to = user.getEmailAddress();

		String emailSubject = ctx.cfg().getConfirmAccountEmailSubject();
		String emailBody = ctx.cfg().getConfirmAccountEmailBody() + "\n\n";
		emailBody += SysConfig.getSiteUrlAsString() + "confirm?code=" + user.getRole(UserRole.class).getConfirmationCode() + "\n\n";
		emailBody += ctx.cfg().getGeneralEmailRegards() + "\n\n----\n";
		emailBody += ctx.cfg().getGeneralEmailFooter() + "\n\n";

		EmailService emailService = EmailServiceManager.getDefaultService();
		emailService.sendEmailIgnoreException(from, to, ctx.cfg().getAuditEmailAddress(), emailSubject, emailBody);
	}
	
	/**
	 * 
	 */
	public void saveUser(Client user) {
		try {
			updateObject(user, getUpdatingStatement("SELECT * FROM users WHERE id = ?"));
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
	}
	
	/**
	 * 
	 */
	public void removeUser(Client user) {
		assertHasUserRole(user);

		saveUser(user);
		users.remove(user.getRole(UserRole.class).getNameAsTag());
	}
	
	/**
	 * 
	 */
	public void saveUsers() {
		try {
			updateObjects(users.values(), getUpdatingStatement("SELECT * FROM users WHERE id = ?"));
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
	}
	
	/**
	 * 
	 */
	public Client getUserByEmailAddress(String emailAddress) {
		return getUserByEmailAddress(EmailAddress.getFromString(emailAddress));
	}

	/**
	 * 
	 */
	public Client getUserByEmailAddress(EmailAddress emailAddress) {
		Client result = null;
		try {
			result = (Client) readObject(getReadingStatement("SELECT * FROM users WHERE email_address = ?"), emailAddress.asString());
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		if (result != null) {
			Client current = doGetUserByTag(result.getRole(UserRole.class).getNameAsTag());
			if (current == null) {
				doAddUser(result);
			} else {
				result = current;
			}
		}

		return result;
	}
	
	/**
	 * 
	 * @methodtype assertion
	 */
	protected void assertHasUserRole(Client user) {
		if (!user.hasRole(UserRole.class)) {
			throw new IllegalArgumentException("User is only a guest");
		}
	}
	
	/**
	 * 
	 * @methodtype assertion
	 */
	protected void assertIsUnknownUserAsIllegalArgument(Client user) {
		if (hasUserByTag(user.getRole(UserRole.class).getNameAsTag())) {
			throw new IllegalArgumentException(user.getRole(UserRole.class).getName() + "is already known");
		}
	}
	
	/**
	 * 
	 * @methodtype assertion
	 */
	protected void assertIsUnknownUserAsIllegalState(Client user) {
		if (hasUserByTag(user.getRole(UserRole.class).getNameAsTag())) {
			throw new IllegalStateException(user.getRole(UserRole.class).getName() + "should not be known");
		}
	}
	
}
