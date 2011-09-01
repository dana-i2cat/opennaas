package net.i2cat.luminis.commandsets.wonesys.test.mock;

import net.i2cat.luminis.protocols.wonesys.WonesysProtocolSession;
import net.i2cat.nexus.resources.protocol.IProtocolMessageFilter;
import net.i2cat.nexus.resources.protocol.IProtocolSessionListener;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

public class SessionMock extends WonesysProtocolSession {

	ProteusMock	proteus	= new ProteusMock();

	public SessionMock(ProtocolSessionContext protocolSessionContext, String sessionID) throws ProtocolException {
		super(protocolSessionContext, sessionID);

	}

	public Status getStatus() {
		return Status.CONNECTED;
	}

	public void connect() throws ProtocolException {
		// TODO Auto-generated method stub

	}

	public void disconnect() throws ProtocolException {
		// TODO Auto-generated method stub

	}

	public Object sendReceive(Object message) throws ProtocolException {
		return proteus.execCommand((String) message);
	}

	public void asyncSend(Object message) throws ProtocolException {
		throw new ProtocolException("Unsupported Operation");

	}

	public void registerProtocolSessionListener(IProtocolSessionListener listener, IProtocolMessageFilter filter, String idListener) {
		// TODO Auto-generated method stub

	}

	public void unregisterProtocolSessionListener(IProtocolSessionListener listener, String idListener) {
		// TODO Auto-generated method stub

	}

}
