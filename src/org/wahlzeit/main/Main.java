package org.wahlzeit.main;

public interface Main {

	/**
	 * 
	 */
	void requestStop();

	/**
	 * 
	 */
	boolean isShuttingDown();

	/**
	 * 
	 */
	void run();

}