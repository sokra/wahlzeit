package org.wahlzeit.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.services.Persistent;

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
		
	}
	
	/**
	 * 
	 */
	public void writeOn(ResultSet rset) throws SQLException {
		
	}
	
	/**
	 * 
	 */
	public void writeId(PreparedStatement stmt, int pos) throws SQLException {
		
	}
}