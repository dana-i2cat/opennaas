package net.i2cat.mantychore.queuemanager.mock;

import net.i2cat.nexus.protocols.sessionmanager.IProtocolMessageFilter;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionListener;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockProtocolSession implements IProtocolSession {
	static Logger	log	= LoggerFactory
								.getLogger(MockProtocolSession.class);


	public void asyncSend(Object arg0) throws ProtocolException {
		// TODO Auto-generated method stub

	}


	public void connect() throws ProtocolException {
		log.info("Connecting...");

	}


	public void disconnect() throws ProtocolException {
		log.info("Disconnectiong...");
	}


	public ProtocolSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSessionID() {
		// TODO Auto-generated method stub
		return null;
	}


	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}


	public Object sendReceive(Object inputMessage) throws ProtocolException {
		String message = (String) inputMessage;
		log.info("---------------------------------------");
		log.info(message);
		log.info("---------------------------> It was sent");
		return new Boolean(true);
	}


	public void registerProtocolSessionListener(IProtocolSessionListener arg0, IProtocolMessageFilter arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	public void unregisterProtocolSessionListener(IProtocolSessionListener arg0, String arg1) {
		// TODO Auto-generated method stub

	}

}
