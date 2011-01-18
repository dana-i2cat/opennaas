package net.i2cat.mantychore.queuemanager.mock;

import com.iaasframework.protocolsessionmanager.IProtocolSession;

public class MockProtocolWrapper {

	public IProtocolSession getProtocolSession(String resourceId, String protocolId) {
		return new MockProtocolSession();
	}

}
