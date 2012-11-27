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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import org.wahlzeit.services.ContextProvider;
import org.wahlzeit.services.EmailAddress;
import org.wahlzeit.services.EmailServer;
import org.wahlzeit.services.ObjectManager;
import org.wahlzeit.services.SysConfig;
import org.wahlzeit.services.SysLog;


/**
 * The UserManager provides access to and manages Users (including Moderators and Administrators).
 * 
 * @author dirkriehle
 *
 */
public class UserManager extends ObjectManager implements Saveable {
	
	@Inject
	protected SysConfig sysConfig;
	
	@Inject
	protected EmailServer emailServer;
	
	@Inject
	protected User.Factory userFactory;
	
	@Inject
	protected Moderator.Factory moderatorFactory;
	
	@Inject
	protected Administrator.Factory administratorFactory;

	/**
	 * 
	 */
	@Inject
	public UserManager(SysLog sysLog, ContextProvider contextProvider) {
		super(sysLog, contextProvider);
	}

	/**
	 * Maps nameAsTag to user of that name (as tag)
	 */
	protected Map<String, User> users = new HashMap<String, User>();
	
	/**
	 * 
	 */
	protected Random codeGenerator = new Random(System.currentTimeMillis());

	/**
	 * 
	 */
	public boolean hasUserByName(String name) {
		return hasUserByTag(Tags.asTag(name));
	}
	
	/**
	 * 
	 */
	public boolean hasUserByTag(String tag) {
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
	public User getUserByName(String name) {
		return getUserByTag(Tags.asTag(name));
	}
	
	/**
	 * 
	 */
	public User getUserByTag(String tag) {
		User result = doGetUserByTag(tag);

		if (result == null) {
			try {
				result = (User) readObject(getReadingStatement("SELECT * FROM users WHERE name_as_tag = ?"), tag);
			} catch (SQLException sex) {
				sysLog.logThrowable(sex);
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
	protected User doGetUserByTag(String tag) {
		return users.get(tag);
	}
	
	/**
	 * 
	 * @methodtype factory
	 */
	protected User createObject(ResultSet rset) throws SQLException {
		User result = null;

		AccessRights rights = AccessRights.getFromInt(rset.getInt("rights"));
		if (rights == AccessRights.USER) {
			result = userFactory.create(rset);
		} else if (rights == AccessRights.MODERATOR) {
			result = moderatorFactory.create(rset);
		} else if (rights == AccessRights.ADMINISTRATOR) {
			result = administratorFactory.create(rset);
		} else {
			sysLog.logInfo("received NONE rights value");
		}

		return result;
	}
	
	/**
	 * 
	 */
	public void addUser(User user) {
		assertIsNewUser(user);

		try {
			int id = user.getId();
			createObject(user, getReadingStatement("INSERT INTO users(id) VALUES(?)"), id);
		} catch (SQLException sex) {
			sysLog.logThrowable(sex);
		}
		
		doAddUser(user);		
	}
	
	/**
	 * 
	 */
	protected void doAddUser(User user) {
		users.put(user.getNameAsTag(), user);
	}
	
	/**
	 * 
	 */
	public void removeUser(User user) {
		doRemoveUser(user);

		try {
			deleteObject(user, getReadingStatement("DELETE FROM users WHERE id = ?"));
		} catch (SQLException sex) {
			sysLog.logThrowable(sex);
		}		
	}
	
	/**
	 * 
	 */
	protected void doRemoveUser(User user) {
		users.remove(user.getNameAsTag());
	}
	
	/**
	 * 
	 */
	public void loadUsers(Collection<User> result) {
		try {
			readObjects(result, getReadingStatement("SELECT * FROM users"));
			for (Iterator<User> i = result.iterator(); i.hasNext(); ) {
				User user = i.next();
				if (!doHasUserByTag(user.getNameAsTag())) {
					doAddUser(user);
				} else {
					sysLog.logValueWithInfo("user", user.getName(), "user had already been loaded");
				}
			}
		} catch (SQLException sex) {
			sysLog.logThrowable(sex);
		}
		
		sysLog.logInfo("loaded all users");
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
	public void emailWelcomeMessage(UserSession ctx, User user) {
		EmailAddress from = ctx.cfg().getAdministratorEmailAddress();
		EmailAddress to = user.getEmailAddress();

		String emailSubject = ctx.cfg().getWelcomeEmailSubject();
		String emailBody = ctx.cfg().getWelcomeEmailBody() + "\n\n";
		emailBody += ctx.cfg().getWelcomeEmailUserName() + user.getName() + "\n\n"; 
		emailBody += ctx.cfg().getConfirmAccountEmailBody() + "\n\n";
		emailBody += sysConfig.getSiteUrlAsString() + "confirm?code=" + user.getConfirmationCode() + "\n\n";
		emailBody += ctx.cfg().getGeneralEmailRegards() + "\n\n----\n";
		emailBody += ctx.cfg().getGeneralEmailFooter() + "\n\n";

		emailServer.sendEmail(from, to, ctx.cfg().getAuditEmailAddress(), emailSubject, emailBody);
	}
	
	/**
	 * 
	 */
	public void emailConfirmationRequest(UserSession ctx, User user) {
		EmailAddress from = ctx.cfg().getAdministratorEmailAddress();
		EmailAddress to = user.getEmailAddress();

		String emailSubject = ctx.cfg().getConfirmAccountEmailSubject();
		String emailBody = ctx.cfg().getConfirmAccountEmailBody() + "\n\n";
		emailBody += sysConfig.getSiteUrlAsString() + "confirm?code=" + user.getConfirmationCode() + "\n\n";
		emailBody += ctx.cfg().getGeneralEmailRegards() + "\n\n----\n";
		emailBody += ctx.cfg().getGeneralEmailFooter() + "\n\n";

		emailServer.sendEmail(from, to, ctx.cfg().getAuditEmailAddress(), emailSubject, emailBody);
	}
	
	/**
	 * 
	 */
	public void saveUser(User user) {
		try {
			updateObject(user, getUpdatingStatement("SELECT * FROM users WHERE id = ?"));
		} catch (SQLException sex) {
			sysLog.logThrowable(sex);
		}
	}
	
	/**
	 * 
	 */
	public void dropUser(User user) {
		saveUser(user);
		users.remove(user.getNameAsTag());
	}
	
	/**
	 * 
	 */
	public void saveUsers() {
		try {
			updateObjects(users.values(), getUpdatingStatement("SELECT * FROM users WHERE id = ?"));
		} catch (SQLException sex) {
			sysLog.logThrowable(sex);
		}
	}
	
	@Override
	public void save() {
		saveUsers();
	}
	
	/**
	 * 
	 */
	public User getUserByEmailAddress(String emailAddress) {
		return getUserByEmailAddress(EmailAddress.getFromString(emailAddress));
	}

	/**
	 * 
	 */
	public User getUserByEmailAddress(EmailAddress emailAddress) {
		User result = null;
		try {
			result = (User) readObject(getReadingStatement("SELECT * FROM users WHERE email_address = ?"), emailAddress.asString());
		} catch (SQLException sex) {
			sysLog.logThrowable(sex);
		}
		
		if (result != null) {
			User current = doGetUserByTag(result.getNameAsTag());
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
	protected void assertIsNewUser(User user) {
		if (hasUserByTag(user.getNameAsTag())) {
			throw new IllegalStateException("User already exists!");
		}
	}
	
}
