package net.i2cat.mantychore.protocols.sessionmanager.mock;

import net.i2cat.mantychore.protocols.sessionmanager.IProtocolSession;
import net.i2cat.mantychore.protocols.sessionmanager.IProtocolSessionFactory;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolException;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockProtocolSessionFactory implements IProtocolSessionFactory {

	/** The logger **/
	Logger	logger	= LoggerFactory.getLogger(MockProtocolSessionFactory.class);

	public MockProtocolSessionFactory() {
		super();
		logger.info("Mock Netconf Protocol Session Factory created");
	}

	@Override
	public IProtocolSession createProtocolSession(String sessionID, ProtocolSessionContext protocolSessionContext) throws ProtocolException {

		return new MockProtocolSession();
	}
}
