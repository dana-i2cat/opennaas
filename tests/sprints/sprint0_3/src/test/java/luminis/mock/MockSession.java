package luminis.mock;

import net.i2cat.nexus.resources.protocol.IProtocolMessageFilter;
import net.i2cat.nexus.resources.protocol.IProtocolSession;
import net.i2cat.nexus.resources.protocol.IProtocolSessionListener;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

public class MockSession implements IProtocolSession {

	String					sessionID;
	ProtocolSessionContext	protocolSessionContext;

	public MockSession(String sessionID, ProtocolSessionContext protocolSessionContext) {
		this.sessionID = sessionID;
		this.protocolSessionContext = protocolSessionContext;
	}

	@Override
	public void asyncSend(Object arg0) throws ProtocolException {
		// TODO Auto-generated method stub

	}

	@Override
	public void connect() throws ProtocolException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() throws ProtocolException {
		// TODO Auto-generated method stub

	}

	@Override
	public ProtocolSessionContext getSessionContext() {
		return protocolSessionContext;
	}

	@Override
	public String getSessionId() {

		return sessionID;
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerProtocolSessionListener(IProtocolSessionListener arg0, IProtocolMessageFilter arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object sendReceive(Object arg0) throws ProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSessionContext(ProtocolSessionContext arg0) {
		this.protocolSessionContext = arg0;

	}

	@Override
	public void setSessionId(String arg0) {
		this.sessionID = arg0;

	}

	@Override
	public void unregisterProtocolSessionListener(IProtocolSessionListener arg0, String arg1) {
		// TODO Auto-generated method stub

	}

}
