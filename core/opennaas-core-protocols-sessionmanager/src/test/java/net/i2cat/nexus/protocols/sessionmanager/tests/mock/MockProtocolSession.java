package net.i2cat.nexus.protocols.sessionmanager.tests.mock;

import net.i2cat.nexus.resources.protocol.IProtocolMessageFilter;
import net.i2cat.nexus.resources.protocol.IProtocolSession;
import net.i2cat.nexus.resources.protocol.IProtocolSessionListener;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MockProtocolSession implements IProtocolSession {
	static Log						log			= LogFactory.getLog(MockProtocolSession.class);

	private ProtocolSessionContext	protocolSessionContext;

	String							sessionId	= "mockEmptyID";

	public MockProtocolSession() {
	}

	@Override
	public void asyncSend(Object arg0) throws ProtocolException {
		// TODO Auto-generated method stub
	}

	@Override
	public void connect() throws ProtocolException {
		log.info("Connecting...");
	}

	@Override
	public void disconnect() throws ProtocolException {
		log.info("Disconnectiong...");
	}

	@Override
	public ProtocolSessionContext getSessionContext() {
		return protocolSessionContext;
	}

	@Override
	public void setSessionContext(ProtocolSessionContext protocolSessionContext) {
		this.protocolSessionContext = protocolSessionContext;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public Status getStatus() {
		return Status.CONNECTED;
	}

	@Override
	public Object sendReceive(Object inputMessage) throws ProtocolException {
		return "fake reply";
	}

	@Override
	public void registerProtocolSessionListener(IProtocolSessionListener arg0, IProtocolMessageFilter arg1, String arg2) {
	}

	@Override
	public void unregisterProtocolSessionListener(IProtocolSessionListener arg0, String arg1) {
	}

	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;

	}
}
