package org.opennaas.extensions.transports.virtual;

/*
 * #%L
 * OpenNaaS :: Transport :: Virtual
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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.opennaas.core.resources.transport.IMessageTransport;
import org.opennaas.core.resources.transport.IMessageTransportListener;
import org.opennaas.core.resources.transport.IStreamTransport;
import org.opennaas.core.resources.transport.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualTransport implements IStreamTransport, IMessageTransport {

	public static final String			VIRTUAL						= "Virtual";

	private Logger						logger						= LoggerFactory.getLogger(VirtualTransport.class);
	private IVirtualTransportProvider	virtualTransportProvider	= null;
	private PipedInputStream			inputStream					= null;
	private PipedOutputStream			outputStream				= null;

	public VirtualTransport(IVirtualTransportProvider virtualTransportProvider) throws TransportException {
		this.virtualTransportProvider = virtualTransportProvider;
		if (this.virtualTransportProvider == null) {
			throw new TransportException("Could not find a VirtualTransportProvider for this virtual transport");
		}

		outputStream = new PipedOutputStream();
		try {
			inputStream = new PipedInputStream(outputStream);
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new TransportException(ex);
		}
	}

	public void connect() throws TransportException {
		logger.info("Virtual transport connected");
	}

	public void disconnect() throws TransportException {
		logger.info("Virtual transport disconnected");
	}

	public InputStream getInputStream() throws TransportException {
		return inputStream;
	}

	public OutputStream getOutputStream() throws TransportException {
		return outputStream;
	}

	public void send(byte[] message) throws TransportException {
		logger.debug("Sending message " + message);
		byte[] response = virtualTransportProvider.getStreamTransportReponse(message);
		try {
			outputStream.write(response);
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new TransportException(ex);
		}
	}

	public void send(char[] message) throws TransportException {
		byte[] request = new byte[message.length];
		for (int i = 0; i < message.length; i++) {
			request[i] = (byte) message[i];
		}
		logger.debug("Sending message " + request);
		byte[] response = virtualTransportProvider.getStreamTransportReponse(request);
		try {
			outputStream.write(response);
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new TransportException(ex);
		}
	}

	public Object sendMessage(Object message) {
		logger.debug("Virtual transport sending message " + message.toString());
		return virtualTransportProvider.getMessageTransportResponse(message);
	}

	public void subscribe(IMessageTransportListener messageTransportListener) {
		logger.debug("Message transport listener added");
	}

	public void unsubscribe(IMessageTransportListener messageTransoprtListener) {
		logger.debug("Message transport listener removed");
	}
}