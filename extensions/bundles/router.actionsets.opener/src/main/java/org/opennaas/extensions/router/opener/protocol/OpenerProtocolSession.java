package org.opennaas.extensions.router.opener.protocol;

import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.protocol.IProtocolSession.Status;
import org.opennaas.extensions.router.opener.client.OpenerClient;

public class OpenerProtocolSession implements IProtocolSession {
	
	private ProtocolSessionContext					sessionContext	= null;
	private String									sessionId				= null;
	private Status									status					= null;
	
	private Map<String, IProtocolSessionListener>	protocolListeners		= null;
	private Map<String, IProtocolMessageFilter>		protocolMessageFilters	= null;
	
	private OpenerClient client;

	public OpenerProtocolSession(String sessionID,
			ProtocolSessionContext protocolSessionContext) {
		super();
		setSessionId(sessionID);
		setSessionContext(protocolSessionContext);
		
		this.protocolListeners = new HashMap<String, IProtocolSessionListener>();
		this.protocolMessageFilters = new HashMap<String, IProtocolMessageFilter>();
		
		this.status = Status.DISCONNECTED_BY_USER;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public ProtocolSessionContext getSessionContext() {
		return sessionContext;
	}

	@Override
	public void setSessionContext(ProtocolSessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public void connect() throws ProtocolException {
		if (status.equals(Status.CONNECTED)) {
			throw new ProtocolException(
					"Cannot connect because the session is already connected");
		}
		initializeClient();
		setStatus(Status.CONNECTED);
	}

	@Override
	public void disconnect() throws ProtocolException {
		if (!status.equals(Status.CONNECTED)) {
			throw new ProtocolException(
					"Cannot disconnect because the session is already disconnected. Current state: "
							+ status);
		}
		stopClient();
		setStatus(Status.DISCONNECTED_BY_USER);
	}

	@Override
	public Object sendReceive(Object message) throws ProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void asyncSend(Object message) throws ProtocolException {
		throw new ProtocolException("Unsuported operation");
	}

	@Override
	public void registerProtocolSessionListener(
			IProtocolSessionListener protocolSessionListener,
			IProtocolMessageFilter protocolMessageFilter, String idListener) {
		protocolMessageFilters.put(idListener, protocolMessageFilter);
		protocolListeners.put(idListener, protocolSessionListener);
	}

	@Override
	public void unregisterProtocolSessionListener(
			IProtocolSessionListener protocolSessionListener, String idListener) {
		protocolMessageFilters.remove(idListener);
		protocolListeners.remove(idListener);

	}
	
	protected void setStatus(Status status){
		this.status = status;
	}
	
	protected void initializeClient() {
		client = new OpenerClient();
	}
	
	protected void stopClient() {
		client = null;
	}

}
