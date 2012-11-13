package org.opennaas.core.resources.mock;

import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionFactory;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MockProtocolSessionFactory implements IProtocolSessionFactory {

	/** The logger **/
	Log	logger	= LogFactory.getLog(MockProtocolSessionFactory.class);

	public MockProtocolSessionFactory() {
		super();
		logger.info("Mock Protocol Session Factory created");
	}

	public IProtocolSession createProtocolSession(String sessionID, ProtocolSessionContext protocolSessionContext) throws ProtocolException {
		IProtocolSession session = new MockProtocolSession();
		session.setSessionId(sessionID);
		return session;
	}
}
