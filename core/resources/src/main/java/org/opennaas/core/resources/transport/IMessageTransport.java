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

import java.io.IOException;

/**
 * An interface for Message based transports. These transports receive individual messages from the communications channel (either synchronous
 * responses or asynchronous events) instead of receiving a continuous stream of characters or bytes
 * 
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
 *          </p>
 */
public interface IMessageTransport extends ITransport {

	/**
	 * Sends a raw object to the device
	 * 
	 * @param rawInput
	 *            the object to send
	 * @throws IOException
	 */
	public Object sendMessage(Object message);

	/**
	 * Subscribers will get asynchronous messages
	 * 
	 * @param listener
	 */
	public void subscribe(IMessageTransportListener listener);

	/**
	 * Cancel the subscription to asynchronous messages
	 * 
	 * @param listener
	 */
	public void unsubscribe(IMessageTransportListener listener);
}
