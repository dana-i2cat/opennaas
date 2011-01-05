package net.i2cat.mantychore.tests.protocols.netconf;

import com.iaasframework.protocolsessionmanager.IProtocolSession;
import com.iaasframework.protocolsessionmanager.IProtocolSessionFactory;
import com.iaasframework.protocolsessionmanager.ProtocolException;
import com.iaasframework.protocolsessionmanager.ProtocolSessionContext;

public class MockProtocolFactory implements IProtocolSessionFactory {

	@Override
	public IProtocolSession createProtocolSession(String sessionID, ProtocolSessionContext protocolSessionContext) throws ProtocolException {

		IProtocolSession protocolSession = new MockProtocolSession();
		return protocolSession;
	}

}
