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

import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class MockProtocolSessionManager extends ProtocolSessionManager {

	private String				resourceId		= "pedrosa";
	private String				hostIpAddress	= "10.10.80.11";
	private String				hostPort		= "27773";

	private IProtocolSession	session			= null;

	public MockProtocolSessionManager(String deviceID) {
		super(deviceID);
		// TODO Auto-generated constructor stub
	}

	public synchronized IProtocolSession obtainSessionByProtocol(String protocol, boolean lock) throws ProtocolException {
		if (protocol == null)
			throw new ProtocolException("Requested protocol is null.");

		IProtocolSession session = getSession(resourceId, hostIpAddress, hostPort);

		session.connect();
		return session;

	}

	private IProtocolSession getSession(String resourceId, String hostIpAddress, String hostPort) throws ProtocolException {

		// get WonesysProtocolSession using ProtocolSessionManager
		ProtocolSessionContext sessionContext = createWonesysProtocolSessionContext(hostIpAddress, hostPort);
		IProtocolSession protocolSession = getProtocolSession(resourceId, sessionContext);
		if (protocolSession == null)
			throw new ProtocolException("Could not get a valid ProtocolSession");

		return protocolSession;
	}

	private ProtocolSessionContext createWonesysProtocolSessionContext(String ip,
			String port) {

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"wonesys");
		protocolSessionContext.addParameter("protocol.mock", "true");
		return protocolSessionContext;
	}

	private IProtocolSession getProtocolSession(String resourceId, ProtocolSessionContext sessionContext) throws ProtocolException {

		if (session == null)
			session = new SessionMock(sessionContext, resourceId + "01");

		return session;
	}

}
