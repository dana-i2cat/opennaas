package net.i2cat.nexus.protocols.sessionmanager.tests.mock;

import net.i2cat.nexus.resources.protocol.IProtocolSession;
import net.i2cat.nexus.resources.protocol.IProtocolSessionFactory;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

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
