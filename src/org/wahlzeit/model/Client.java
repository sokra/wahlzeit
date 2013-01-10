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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.wahlzeit.services.EmailAddress;
import org.wahlzeit.services.SysLog;
import org.wahlzeit.services.persistence.AbstractPersistent;
import org.wahlzeit.services.persistence.EmailAddressPersistor;
import org.wahlzeit.services.persistence.Persist;
import org.wahlzeit.services.persistence.Persistent;
import org.wahlzeit.utils.StringUtil;


/**
 * A Client uses the system. It is an abstract superclass.
 * This package defines guest, user, moderator, and administrator clients.
 * 
 * @author dirkriehle
 *
 */
public class Client extends AbstractPersistent {
	
	/**
	 * 
	 */
	protected Collection<ClientRole> roles = new ArrayList<ClientRole>();
	
	/**
	 * 
	 */
	@Persist(persistor=EmailAddressPersistor.class)
	protected EmailAddress emailAddress = EmailAddress.EMPTY;

	/**
	 * 
	 */
	public Client() {
		// Do nothing
	}
	
	/**
	 * 
	 */
	public Client(ClientRole myRole) {
		addRole(myRole);
	}


	/**
	 * @methodtype get
	 */
	@SuppressWarnings("unchecked")
	public <T extends ClientRole> T getRole(Class<T> roleClass) {
		if(roleClass == null)
			throw new IllegalArgumentException("roleClass must be set");
		for(ClientRole role: roles)
			if(roleClass.isInstance(role))
				return (T) role;
		return null;
	}
	
	/**
	 *
	 */
	public void addRole(ClientRole role) {
		if(role == null)
			throw new IllegalArgumentException("role must not be null");
		roles.add(role);
		role.onConnectedWithClient(this);
	}
	
	/**
	 *
	 */
	public void removeRole(ClientRole role) {
		if(role == null)
			throw new IllegalArgumentException("role must not be null");
		roles.remove(role);
	}
	
	/**
	 *
	 */
	public void removeRole(Class<? extends ClientRole> roleClass) {
		if(roleClass == null)
			throw new IllegalArgumentException("roleClass must not be null");
		for(Iterator<ClientRole> i = roles.iterator(); i.hasNext();) {
			ClientRole role = i.next();
			if(roleClass.isInstance(role))
				i.remove();
		}
	}
	
	/**
	 * 
	 * @methodtype boolean-query
	 */
	public boolean hasRole(Class<? extends ClientRole> roleClass) {
		if(roleClass == null)
			throw new IllegalArgumentException("roleClass must be set");
		for(ClientRole role: roles)
			if(roleClass.isInstance(role))
				return true;
		return false;
	}
	
	/**
	 * 
	 */
	public void setEmailAddress(String myEmailAddress) {
		setEmailAddress(EmailAddress.getFromString(myEmailAddress));
	}
	
	/**
	 * 
	 */
	public void setEmailAddress(EmailAddress myEmailAddress) {
		emailAddress = myEmailAddress;
		incWriteCount();
		
		for(ClientRole role: roles)
			role.onClientChanged();
	}
	
	/**
	 * 
	 */
	public EmailAddress getEmailAddress() {
		return emailAddress;
	}
	
	/**
	 * 
	 */
	@Override
	public void readFrom(ResultSet rset) throws SQLException {
		super.readFrom(rset);
		Collection<String> roleNames = StringUtil.split(rset.getString("roles"), " ");
		roles.clear();
		for(String roleName: roleNames) {
			ClientRole role = ClientRoleHelper.constructRoleFromName(roleName);
			if(role == null) {
				SysLog.logInfo("received NONE role value");
			} else {
				roles.add(role);
				role.onConnectedWithClient(this);
				if(role instanceof Persistent)
					((Persistent)role).readFrom(rset);
			}
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void writeOn(ResultSet rset) throws SQLException {
		super.writeOn(rset);
		Collection<String> roleNames = new ArrayList<String>();
		for(ClientRole role: roles)
			roleNames.add(ClientRoleHelper.getRoleName(role));
		rset.updateString("roles", StringUtil.join(roleNames, " "));
		for(ClientRole role: roles)
			if(role instanceof Persistent)
				((Persistent)role).writeOn(rset);
	}
	
	@Override
	public void writeId(PreparedStatement stmt, int pos) throws SQLException {
		for(ClientRole role: roles)
			if(role instanceof Persistent)
				((Persistent)role).writeId(stmt, pos);
	}

}
