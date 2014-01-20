package org.opennaas.extensions.roadm.wonesys.transports;

/*
 * #%L
 * OpenNaaS :: ROADM :: W-Onesys Protocol
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSessionContextUtils;
import org.opennaas.extensions.roadm.wonesys.transports.rawsocket.RawSocketTransport;

public class WonesysTransport extends RawSocketTransport implements ITransport {

	/** The logger **/
	Log				log		= LogFactory.getLog(WonesysTransport.class);

	/**
	 * Device identifier needed to call EMSModule
	 */
	private String	hostIp	= null;
	private Integer	port	= null;

	public WonesysTransport(ProtocolSessionContext protocolSessionContext) throws ProtocolException {

		super();

		this.hostIp = WonesysProtocolSessionContextUtils.getHost(protocolSessionContext);
		this.port = WonesysProtocolSessionContextUtils.getPort(protocolSessionContext);

		if (this.hostIp == null || this.port == -1)
			throw new ProtocolException("Could not extract required data from given ProtocolSessionContext");
	}

	@Override
	public Object sendMsg(Object message) throws WonesysTransportException {
		sendAsync(message);
		return null;
	}

	@Override
	public void sendAsync(Object message) throws WonesysTransportException {
		String toSend = (String) message;

		try {
			log.debug("Sending message to " + hostIp + ":" + port);
			sendAsyncToSocket(toSend);
		} catch (IOException ioe) {
			throw new WonesysTransportException(ioe);
		}
	}

	@Override
	public void connect() throws WonesysTransportException {

		try {
			log.debug("Connecting to " + hostIp + ":" + port);
			connectSocket(hostIp, port);
		} catch (IOException e) {
			throw new WonesysTransportException(e);
		}
	}

	@Override
	public void disconnect() throws WonesysTransportException {
		try {
			log.debug("Disconnecting from " + hostIp + ":" + port);
			disconnectSocket();
		} catch (IOException e) {
			throw new WonesysTransportException(e);
		}
	}

	// @Override
	// public void registerListener(ITransportListener listener) {
	// registerListener((EventHandler) listener);
	// }
	//
	// @Override
	// public void unregisterListener(ITransportListener listener) {
	// unregisterListener((EventHandler) listener);
	// }
}
