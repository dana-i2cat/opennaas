package org.opennaas.extensions.abno.capability.linkprovisioning.protocol;

/*
 * #%L
 * OpenNaaS :: XIFI ABNO
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.abno.client.ABNOClientFactory;
import org.opennaas.extensions.abno.client.IABNOClient;

/**
 * ABNO {@link IProtocolSession}
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class ABNOProtocolSession implements IProtocolSession {

	public static final String						ABNO_PROTOCOL_TYPE		= "abno";

	private ProtocolSessionContext					sessionContext			= null;
	private String									sessionId				= null;
	private Status									status					= null;

	private Map<String, IProtocolSessionListener>	protocolListeners		= null;
	private Map<String, IProtocolMessageFilter>		protocolMessageFilters	= null;

	private ABNOClientFactory						clientFactory;
	private IABNOClient								abnoClient;

	public ABNOProtocolSession(String sessionID, ProtocolSessionContext protocolSessionContext) throws ProtocolException {
		super();
		setSessionId(sessionID);
		setSessionContext(protocolSessionContext);

		this.protocolListeners = new HashMap<String, IProtocolSessionListener>();
		this.protocolMessageFilters = new HashMap<String, IProtocolMessageFilter>();

		this.clientFactory = new ABNOClientFactory();

		this.status = Status.DISCONNECTED_BY_USER;

		checkProtocolSessionContext(protocolSessionContext);
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public ProtocolSessionContext getSessionContext() {
		return sessionContext;
	}

	@Override
	public void setSessionContext(ProtocolSessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	protected void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public void connect() throws ProtocolException {
		if (status.equals(Status.CONNECTED)) {
			throw new ProtocolException("Cannot connect because the session is already connected");
		}
		this.abnoClient = this.clientFactory.createClient((getSessionContext()));
		this.setStatus(Status.CONNECTED);
	}

	@Override
	public void disconnect() throws ProtocolException {
		if (!status.equals(Status.CONNECTED)) {
			throw new ProtocolException("Cannot disconnect because the session is already disconnected. Current state: " + status);
		}

		this.abnoClient = null;
		setStatus(Status.DISCONNECTED_BY_USER);
	}

	@Override
	public Object sendReceive(Object message) throws ProtocolException {
		throw new ProtocolException("Unsuported operation");
	}

	@Override
	public void asyncSend(Object message) throws ProtocolException {
		throw new ProtocolException("Unsuported operation");
	}

	@Override
	public void registerProtocolSessionListener(IProtocolSessionListener protocolSessionListener, IProtocolMessageFilter protocolMessageFilter,
			String idListener) {
		protocolMessageFilters.put(idListener, protocolMessageFilter);
		protocolListeners.put(idListener, protocolSessionListener);
	}

	@Override
	public void unregisterProtocolSessionListener(IProtocolSessionListener protocolSessionListener, String idListener) {
		protocolMessageFilters.remove(idListener);
		protocolListeners.remove(idListener);
	}

	/**
	 * This method should NOT be used in Actions to retrieve the client. In Actions use {@link #getABNOClient()} instead.
	 * 
	 * @return abnoClient
	 * @see #getABNOClientForUse()
	 */
	public IABNOClient getABNOClient() {
		return abnoClient;
	}

	public void setABNOClient(IABNOClient abnoClient) {
		this.abnoClient = abnoClient;
	}

	public ABNOClientFactory getClientFactory() {
		return clientFactory;
	}

	public void setClientFactory(ABNOClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	/**
	 * Retrieve Client and checks session is connected. This method may be used in Actions to retrieve the client and call its methods afterwards.
	 * 
	 * @return initialized client.
	 * @throws ProtocolException
	 *             if this ProtocolSession is not connected.
	 */
	public IABNOClient getABNOClientForUse() throws ProtocolException {
		if (!status.equals(Status.CONNECTED)) {
			throw new ProtocolException(
					"Cannot use client. Session is not connected. Current session status is " + status);
		}
		return getABNOClient();
	}

	private void checkProtocolSessionContext(ProtocolSessionContext protocolSessionContext) throws ProtocolException {

		String protocol = (String) protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL);
		if ((protocol == null) || (protocol.length() == 0) || !protocol.equals(ABNO_PROTOCOL_TYPE)) {
			throw new ProtocolException(
					"Protocols ABNO: Invalid protocol type: " + protocol + ". Protocol type must be " + ABNO_PROTOCOL_TYPE);
		}

		String uri = (String) protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);
		if ((uri == null) || (uri.length() == 0)) {
			throw new ProtocolException(
					"Protocols ABNO: Couldn't get " + ProtocolSessionContext.PROTOCOL_URI + " from protocolSessionContext.");
		}

		// check given uri is a valid URI
		try {
			new URI(uri);
		} catch (URISyntaxException e) {
			throw new ProtocolException(e);
		}
	}

}
