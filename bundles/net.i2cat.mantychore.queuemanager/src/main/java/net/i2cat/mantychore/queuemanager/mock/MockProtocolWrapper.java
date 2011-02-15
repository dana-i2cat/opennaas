package net.i2cat.mantychore.queuemanager.mock;

import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;

public class MockProtocolWrapper {

	public IProtocolSession getProtocolSession(String resourceId, String protocolId) {
		return new MockProtocolSession();
	}

}
