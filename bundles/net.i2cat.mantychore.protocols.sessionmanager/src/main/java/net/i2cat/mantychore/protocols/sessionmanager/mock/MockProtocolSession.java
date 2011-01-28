package net.i2cat.mantychore.protocols.sessionmanager.mock;

import net.i2cat.mantychore.protocols.sessionmanager.IProtocolMessageFilter;
import net.i2cat.mantychore.protocols.sessionmanager.IProtocolSession;
import net.i2cat.mantychore.protocols.sessionmanager.IProtocolSessionListener;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolException;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.Reply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockProtocolSession implements IProtocolSession {
	static Logger					log	= LoggerFactory
												.getLogger(MockProtocolSession.class);

	private ProtocolSessionContext	protocolSessionContext;

	public MockProtocolSession() {
		protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		protocolSessionContext.addParameter("protocol.uri", "mock://foo:bar@foo:22/netconf");

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
		Query message = (Query) inputMessage;
		log.info("---------------------------------------");
		log.info(message.toXML());
		log.info("---------------------------> It was sent");
		Reply reply = new Reply();
		reply.setOk(true);
		return reply;
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
