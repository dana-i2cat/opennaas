package org.opennaas.core.resources.mock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class MockProtocolSession implements IProtocolSession {
	static Log						log			= LogFactory.getLog(MockProtocolSession.class);

	private ProtocolSessionContext	protocolSessionContext;

	String							sessionId	= "mockEmptyID";

	Status							status		= Status.DISCONNECTED_BY_USER;

	public MockProtocolSession() {
	}

	public void asyncSend(Object arg0) throws ProtocolException {
		// TODO Auto-generated method stub
	}

	public void connect() throws ProtocolException {
		status = Status.CONNECTED;
		log.info("Connecting...");
	}

	public void disconnect() throws ProtocolException {
		status = Status.DISCONNECTED_BY_USER;
		log.info("Disconnectiong...");
	}

	public ProtocolSessionContext getSessionContext() {
		return protocolSessionContext;
	}

	public void setSessionContext(ProtocolSessionContext protocolSessionContext) {
		this.protocolSessionContext = protocolSessionContext;
	}

	public String getSessionId() {
		return sessionId;
	}

	public Status getStatus() {
		return status;
	}

	public Object sendReceive(Object inputMessage) throws ProtocolException {
		return "fake reply";
	}

	public void registerProtocolSessionListener(IProtocolSessionListener arg0, IProtocolMessageFilter arg1, String arg2) {
	}

	public void unregisterProtocolSessionListener(IProtocolSessionListener arg0, String arg1) {
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;

	}
}
