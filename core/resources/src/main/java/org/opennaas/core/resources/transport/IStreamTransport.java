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

import java.io.InputStream;
import java.io.OutputStream;

/**
 * An interface to describe transport types used to send/receive data from the devices. This simply wraps up any Input/Output Streams.
 * <p>
 * Known Limitations: N/A
 * <p>
 * <b>History</b> <br>
 * <ul>
 * <li>2008/05: Initial file creation.</li>
 * </ul>
 * <p>
 * 
 * @author Mathieu Lemay - ITI
 * @version 2.0
 */
public interface IStreamTransport extends ITransport {

	/**
	 * Sends a raw array of bytes to the device
	 * 
	 * @param rawInput
	 *            Sends String to send
	 */
	public void send(byte[] rawInput) throws TransportException;

	/**
	 * Sends a raw array of chars to the device
	 * 
	 * @param rawInput
	 *            Sends String to send
	 */
	public void send(char[] rawInput) throws TransportException;

	/**
	 * Gets the transport input stream
	 */
	public InputStream getInputStream() throws TransportException;

	/**
	 * Getter for the OutputStream of the Transport
	 * 
	 */
	public OutputStream getOutputStream() throws TransportException;
}
