package org.wahlzeit.services;

public interface Session {

	/**
	 * 
	 */
	String getName();

	/**
	 * 
	 */
	boolean hasDatabaseConnection();

	/**
	 * 
	 */
	DatabaseConnection getDatabaseConnection();

	/**
	 * 
	 */
	void dropDatabaseConnection();

	/**
	 * 
	 */
	String getClientName();

	/**
	 * 
	 */
	void resetProcessingTime();

	/**
	 * 
	 */
	void addProcessingTime(long time);

	/**
	 * 
	 */
	long getProcessingTime();

}