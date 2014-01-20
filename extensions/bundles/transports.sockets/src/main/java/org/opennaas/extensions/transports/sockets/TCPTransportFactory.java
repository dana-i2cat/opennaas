package org.opennaas.extensions.transports.sockets;

/*
 * #%L
 * OpenNaaS :: Transport :: Sockets
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

import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.transport.ITransport;
import org.opennaas.core.resources.transport.ITransportConstants;
import org.opennaas.core.resources.transport.ITransportFactory;
import org.opennaas.core.resources.transport.TransportException;

/**
 * Provides a factory method to create TCP transport instances. The following properties need to be present in the capability descriptor: transport =
 * TCP transport.host = <ip_address or hostname> transport.port = <port number>
 */
public class TCPTransportFactory implements ITransportFactory {

	public ITransport createTransportInstance(ProtocolSessionContext protocolSessionContext) throws TransportException {
		String transportId = null;
		try {
			transportId = (String) protocolSessionContext.getSessionParameters().get(ITransportConstants.TRANSPORT);
		} catch (ClassCastException ex) {
			throw new TransportException(ex.getMessage());
		}

		if (transportId == null) {
			throw new TransportException("No transport id has been specified at the resource configuration");
		} else if (transportId.equals(TCPTransport.TCP)) {
			return createTCPTransport(protocolSessionContext);
		} else {
			throw new TransportException("Transport " + transportId + " cannot be created by this factory");
		}
	}

	private ITransport createTCPTransport(ProtocolSessionContext protocolSessionContext) throws TransportException {
		String host = getAndValidateProperty(protocolSessionContext, ITransportConstants.TRANSPORT_HOST);
		String port = getAndValidateProperty(protocolSessionContext, ITransportConstants.TRANSPORT_PORT);
		return new TCPTransport(host, port);
	}

	private String getAndValidateProperty(ProtocolSessionContext protocolSessionContext, String propertyName) throws TransportException {
		String property = null;
		try {
			property = (String) protocolSessionContext.getSessionParameters().get(propertyName);
		} catch (ClassCastException ex) {
			throw new TransportException(ex.getMessage());
		}

		if (property == null) {
			throw new TransportException("Could not create an instance of transport. Property " + propertyName + " has not been specified");
		}

		return property;
	}
}