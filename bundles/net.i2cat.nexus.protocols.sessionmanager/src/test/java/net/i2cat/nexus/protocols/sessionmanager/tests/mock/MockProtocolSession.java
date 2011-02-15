package net.i2cat.nexus.protocols.sessionmanager.tests.mock;

import net.i2cat.nexus.protocols.sessionmanager.IProtocolMessageFilter;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionListener;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockProtocolSession implements IProtocolSession {
	static Logger					log	= LoggerFactory
												.getLogger(MockProtocolSession.class);

	private ProtocolSessionContext	protocolSessionContext;

	public MockProtocolSession() {
		protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL, "mock");
		protocolSessionContext.addParameter("protocol.uri", "mock://foo:bar@foo:22/mock");
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

	public void setSessionContext(ProtocolSessionContext protocolSessionContext) {
		this.protocolSessionContext = protocolSessionContext;

	}

	@Override
	public String getSessionID() {
		// TODO Auto-generated method stub
		return "mockID";
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object sendReceive(Object inputMessage) throws ProtocolException {
		return "fake reply";
	}

	@Override
	public void registerProtocolSessionListener(IProtocolSessionListener arg0, IProtocolMessageFilter arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterProtocolSessionListener(IProtocolSessionListener arg0, String arg1) {
		// TODO Auto-generated method stub

	}
}
