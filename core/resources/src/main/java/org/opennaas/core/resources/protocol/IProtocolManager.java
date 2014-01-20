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

import java.util.List;

/**
 * The manager of the protocol session managers. There's one per IaaS container, and handles the creation and deletion of individual protocol session
 * managers. ProtocolSessionManagers manage the sessions to a single device, identified by deviceId.
 * 
 * @author eduardgrasa
 * 
 */
public interface IProtocolManager {

	/**
	 * Returns a pointer to the IProtocolSessionManager associated to the deviceID
	 * 
	 * @param resourceId
	 * @return
	 * @throws ProtocolException
	 *             if deviceID is null or is not associated to an existing protocol session manager
	 */
	public IProtocolSessionManager getProtocolSessionManager(String resourceId) throws ProtocolException;

	/**
	 * Returns a pointer to the IProtocolSessionManager associated to the deviceID and a context
	 * 
	 * @param resourceId
	 * @param context
	 * @return
	 * @throws ProtocolException
	 *             if deviceID is null or is not associated to an existing protocol session manager
	 */
	public IProtocolSessionManager getProtocolSessionManagerWithContext(String resourceId, ProtocolSessionContext context) throws ProtocolException;

	/**
	 * Removes an existing protocol session manager. Will cause all its protocol sessions to be disconnected.
	 * 
	 * @param resourceId
	 * @throws ProtocolException
	 *             if deviceID is null or is not associated to an existing protocol session manager
	 */
	public void destroyProtocolSessionManager(String resourceId) throws ProtocolException;

	/**
	 * Return all the device ids of the protocol manager
	 * 
	 * @return
	 */
	public List<String> getAllResourceIds();

	/**
	 * Return all protocols registered to the protocol manager
	 * 
	 * @return
	 */
	public List<String> getAllSupportedProtocols();

}
