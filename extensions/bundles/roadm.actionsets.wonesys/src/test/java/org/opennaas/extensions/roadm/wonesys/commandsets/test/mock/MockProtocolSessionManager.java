package org.opennaas.extensions.roadm.wonesys.commandsets.test.mock;

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
