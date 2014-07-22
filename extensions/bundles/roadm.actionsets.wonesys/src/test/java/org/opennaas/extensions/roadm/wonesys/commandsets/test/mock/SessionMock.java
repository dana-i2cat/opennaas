package org.opennaas.extensions.roadm.wonesys.commandsets.test.mock;

/*
 * #%L
 * OpenNaaS :: ROADM :: W-Onesys Actionset
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

import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.roadm.wonesys.transports.mock.ProteusMock;

public class SessionMock implements IProtocolSession {

	ProteusMock						proteus					= new ProteusMock(null);

	private ProtocolSessionContext	protocolSessionContext	= null;
	private String					sessionID				= null;

	public SessionMock(ProtocolSessionContext protocolSessionContext, String sessionID) throws ProtocolException {
		this.protocolSessionContext = protocolSessionContext;
		this.sessionID = sessionID;
	}

	public Status getStatus() {
		return Status.CONNECTED;
	}

	public void connect() throws ProtocolException {
		// TODO Auto-generated method stub

	}

	public void disconnect() throws ProtocolException {
		// TODO Auto-generated method stub

	}

	public Object sendReceive(Object message) throws ProtocolException {
		return proteus.execCommand((String) message);
	}

	public void asyncSend(Object message) throws ProtocolException {
		throw new ProtocolException("Unsupported Operation");

	}

	public void registerProtocolSessionListener(IProtocolSessionListener listener, IProtocolMessageFilter filter, String idListener) {
		// TODO Auto-generated method stub

	}

	public void unregisterProtocolSessionListener(IProtocolSessionListener listener, String idListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public ProtocolSessionContext getSessionContext() {
		return protocolSessionContext;
	}

	@Override
	public String getSessionId() {
		return sessionID;
	}

	@Override
	public void setSessionContext(ProtocolSessionContext sessionContext) {
		this.protocolSessionContext = sessionContext;

	}

	@Override
	public void setSessionId(String sessionID) {
		this.sessionID = sessionID;

	}

}
