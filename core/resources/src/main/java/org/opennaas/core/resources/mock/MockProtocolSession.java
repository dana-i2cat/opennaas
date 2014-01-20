package org.opennaas.core.resources.mock;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class MockProtocolSession implements IProtocolSession {
	static Log						log			= LogFactory.getLog(MockProtocolSession.class);

	private ProtocolSessionContext	protocolSessionContext;

	String							sessionId	= "mockEmptyID";

	Status							status		= Status.DISCONNECTED_BY_USER;

	public MockProtocolSession() {
	}

	public void asyncSend(Object arg0) throws ProtocolException {
		// TODO Auto-generated method stub
	}

	public void connect() throws ProtocolException {
		status = Status.CONNECTED;
		log.info("Connecting...");
	}

	public void disconnect() throws ProtocolException {
		status = Status.DISCONNECTED_BY_USER;
		log.info("Disconnectiong...");
	}

	public ProtocolSessionContext getSessionContext() {
		return protocolSessionContext;
	}

	public void setSessionContext(ProtocolSessionContext protocolSessionContext) {
		this.protocolSessionContext = protocolSessionContext;
	}

	public String getSessionId() {
		return sessionId;
	}

	public Status getStatus() {
		return status;
	}

	public Object sendReceive(Object inputMessage) throws ProtocolException {
		return "fake reply";
	}

	public void registerProtocolSessionListener(IProtocolSessionListener arg0, IProtocolMessageFilter arg1, String arg2) {
	}

	public void unregisterProtocolSessionListener(IProtocolSessionListener arg0, String arg1) {
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;

	}
}
