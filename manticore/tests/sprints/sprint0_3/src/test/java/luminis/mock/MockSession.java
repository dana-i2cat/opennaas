package luminis.mock;

import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

/**
 * MockSession incapable of sending messages nor using listeners
 *
 * @author isart
 *
 */
public class MockSession implements IProtocolSession {

	String					sessionID;
	ProtocolSessionContext	protocolSessionContext;
	Status					status	= Status.DISCONNECTED_BY_USER;

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
		status = Status.CONNECTED;

	}

	@Override
	public void disconnect() throws ProtocolException {
		status = Status.DISCONNECTED_BY_USER;

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
		return status;
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
