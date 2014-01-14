package org.opennaas.core.resources.protocol;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/**
 * Protocol sessions must implement this interface and publish a service that implements this interface to the OSGi registry, with a property called
 * "Protocol" whose value is the name of the protocol that the protocol session implements.
 * 
 * @author eduardgrasa
 * 
 */
public interface IProtocolSessionFactory {

	/**
	 * Create a protocol session and configure it using the information in the ProtocolSessionContext.
	 * 
	 * @param sessionID
	 *            The ID associated to the session, cannot be null
	 * @param context
	 *            the context associated to the session, cannot be null
	 * @return the new protocol session (not connected)
	 * @throws ProtocolException
	 *             if something goes wrong or the information provided to the operation is not correct
	 */
	public IProtocolSession createProtocolSession(String sessionID, ProtocolSessionContext context) throws ProtocolException;
}
