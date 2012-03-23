package net.i2cat.luminis.actions.tests;

import java.util.List;
import java.util.Set;

import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class MockProtocolSessionManager implements IProtocolSessionManager {

	@Override
	public void destroyProtocolSession(String arg0) throws ProtocolException {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> getAllProtocolSessionIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResourceID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLocked(String arg0) throws ProtocolException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IProtocolSession obtainSession(ProtocolSessionContext arg0, boolean arg1) throws ProtocolException {

		return newMockWonesysProtocolSession();
	}

	@Override
	public IProtocolSession obtainSessionById(String arg0, boolean arg1) throws ProtocolException {
		return newMockWonesysProtocolSession();
	}

	@Override
	public IProtocolSession obtainSessionByProtocol(String arg0, boolean arg1) throws ProtocolException {
		return newMockWonesysProtocolSession();
	}

	@Override
	public void registerContext(ProtocolSessionContext arg0) throws ProtocolException {
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseSession(IProtocolSession arg0) throws ProtocolException {
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseSession(String arg0) throws ProtocolException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterContext(String arg0) {
		// TODO Auto-generated method stub

	}

	public IProtocolSession newMockWonesysProtocolSession() {
		ProtocolSessionContext sessionContext = new ProtocolSessionContext();
		sessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession wonesysProtocolSession = null;
		try {
			wonesysProtocolSession = new WonesysProtocolSession(sessionContext, "1");
		} catch (ProtocolException e) {
			// NEVER CAN HAPPEN
			e.printStackTrace();
		}

		return wonesysProtocolSession;
	}

	@Override
	public List<ProtocolSessionContext> getRegisteredContexts() {
		// TODO Auto-generated method stub
		return null;
	}

}
