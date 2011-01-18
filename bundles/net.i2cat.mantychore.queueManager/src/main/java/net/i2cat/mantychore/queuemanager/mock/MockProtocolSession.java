package net.i2cat.mantychore.queuemanager.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.protocolsessionmanager.IProtocolMessageFilter;
import com.iaasframework.protocolsessionmanager.IProtocolSession;
import com.iaasframework.protocolsessionmanager.IProtocolSessionListener;
import com.iaasframework.protocolsessionmanager.ProtocolException;
import com.iaasframework.protocolsessionmanager.ProtocolSessionContext;

public class MockProtocolSession implements IProtocolSession {
	static Logger	log	= LoggerFactory
								.getLogger(MockProtocolSession.class);

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSessionID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerProtocolSessionListener(IProtocolSessionListener arg0, IProtocolMessageFilter arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object sendReceive(Object inputMessage) throws ProtocolException {
		String message = (String) inputMessage;
		log.info("---------------------------------------");
		log.info(message);
		log.info("---------------------------> It was sent");
		return new Boolean(true);
	}

	@Override
	public void unregisterProtocolSessionListener(IProtocolSessionListener arg0) {
		// TODO Auto-generated method stub

	}

}
