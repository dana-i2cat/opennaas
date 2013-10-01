package org.opennaas.extensions.router.opener.protocol;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.protocol.IProtocolSession.Status;
import org.opennaas.extensions.router.opener.client.OpenerQuaggaOpenAPI;

public class OpenerProtocolSession implements IProtocolSession {

	public static final String						OPENER_PROTOCOL_TYPE	= "opener";

	private ProtocolSessionContext					sessionContext			= null;
	private String									sessionId				= null;
	private Status									status					= null;

	private Map<String, IProtocolSessionListener>	protocolListeners		= null;
	private Map<String, IProtocolMessageFilter>		protocolMessageFilters	= null;

	OpenerQuaggaOpenAPI								openerClient;

	public OpenerProtocolSession(String sessionID,
			ProtocolSessionContext protocolSessionContext) throws ProtocolException {
		super();
		setSessionId(sessionID);
		setSessionContext(protocolSessionContext);

		this.protocolListeners = new HashMap<String, IProtocolSessionListener>();
		this.protocolMessageFilters = new HashMap<String, IProtocolMessageFilter>();

		this.status = Status.DISCONNECTED_BY_USER;

		checkProtocolSessionContext(protocolSessionContext);
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
		this.openerClient = instantiateClient(getSessionContext());
		setStatus(Status.CONNECTED);
	}

	@Override
	public void disconnect() throws ProtocolException {
		if (!status.equals(Status.CONNECTED)) {
			throw new ProtocolException(
					"Cannot disconnect because the session is already disconnected. Current state: "
							+ status);
		}

		this.openerClient = null;
		setStatus(Status.DISCONNECTED_BY_USER);
	}

	@Override
	public Object sendReceive(Object message) throws ProtocolException {
		throw new ProtocolException("Unsuported operation");
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

	/**
	 * This method should not be used in Actions to retrieve the client. In Actions use getOpenerClientForUse instead.
	 * 
	 * @return openerClient
	 * @see getOpenerClientForUse()
	 */
	public OpenerQuaggaOpenAPI getOpenerClient() {
		return openerClient;
	}

	public void setOpenerClient(OpenerQuaggaOpenAPI openerClient) {
		this.openerClient = openerClient;
	}

	/**
	 * Retrieve openerClient and checks session is connected. This method may be used in Actions to retrieve the OpenerClient and call its methods
	 * afterwards.
	 * 
	 * @return initialized openerClient.
	 * @throws ProtocolException
	 *             if this ProtocolSession is not connected.
	 */
	public OpenerQuaggaOpenAPI getOpenerClientForUse() throws ProtocolException {
		if (!status.equals(Status.CONNECTED)) {
			throw new ProtocolException(
					"Cannot use client. Session is not connected. Current session status is " + status);
		}
		return getOpenerClient();
	}

	protected void setStatus(Status status) {
		this.status = status;
	}

	private OpenerQuaggaOpenAPI instantiateClient(ProtocolSessionContext sessionContext) {
		String uri = (String) sessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);
		return JAXRSClientFactory.create(uri, OpenerQuaggaOpenAPI.class);
	}

	private void checkProtocolSessionContext(ProtocolSessionContext protocolSessionContext) throws ProtocolException {

		String protocol = (String) protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL);
		if ((protocol == null) || (protocol.length() == 0) || !protocol.equals(OPENER_PROTOCOL_TYPE)) {
			throw new ProtocolException(
					"Protocols OPENER: Invalid protocol type: " + protocol + ". Protocol type must be " + OPENER_PROTOCOL_TYPE);
		}

		String uri = (String) protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);
		if ((uri == null) || (uri.length() == 0)) {
			throw new ProtocolException(
					"Protocols OPENER: Couldn't get " + ProtocolSessionContext.PROTOCOL_URI + " from protocolSessionContext.");
		}

		// check given uri is a valid URI
		try {
			new URI(uri);
		} catch (URISyntaxException e) {
			throw new ProtocolException(e);
		}
	}

}
