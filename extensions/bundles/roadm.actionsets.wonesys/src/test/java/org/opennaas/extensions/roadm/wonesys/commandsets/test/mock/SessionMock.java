package org.opennaas.extensions.roadm.wonesys.commandsets.test.mock;

import org.opennaas.extensions.roadm.wonesys.transports.mock.ProteusMock;
import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class SessionMock implements IProtocolSession {

	ProteusMock						proteus					= new ProteusMock(null);

	private ProtocolSessionContext	protocolSessionContext	= null;
	private String					sessionID				= null;

	public SessionMock(ProtocolSessionContext protocolSessionContext, String sessionID) throws ProtocolException {
		this.protocolSessionContext = protocolSessionContext;
		this.sessionID = sessionID;
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

	@Override
	public ProtocolSessionContext getSessionContext() {
		return protocolSessionContext;
	}

	@Override
	public String getSessionId() {
		return sessionID;
	}

	@Override
	public void setSessionContext(ProtocolSessionContext sessionContext) {
		this.protocolSessionContext = sessionContext;

	}

	@Override
	public void setSessionId(String sessionID) {
		this.sessionID = sessionID;

	}

}
