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