package org.opennaas.core.resources.transport;

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
 * An interface to describe transport types used to send/receive data from the devices.
 * 
 * <p>
 * Known Limitations: N/A
 * <p>
 * <b>History</b> <br>
 * <ul>
 * <li>2008/05: Changed to fit IaaS Engine.</li>
 * <li>2006/04: Change in the transport to be integrated in the engine.</li>
 * <li>2003/08: Initial file creation.</li>
 * </ul>
 * <p>
 * Communications Research Centre and University of Ottawa - Copyright &copy 2003 <br>
 * </p>
 * 
 * @author Mathieu Lemay - Research Technologist, Communications Research Centre
 * @version 2.0
 */

public interface ITransport
{
	/**
	 * Connects to the device
	 * 
	 * @throws TransportException
	 */
	public void connect() throws TransportException;

	/**
	 * Disconnects from the device
	 * 
	 * @throws TransportException
	 * */
	public void disconnect() throws TransportException;

}
