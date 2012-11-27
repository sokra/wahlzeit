package org.wahlzeit.services;

import javax.inject.Provider;

public interface ContextProvider extends Provider<Session> {

	/**
	 * 
	 */
	void set(Session ctx);

	/**
	 * 
	 */
	void drop();

}