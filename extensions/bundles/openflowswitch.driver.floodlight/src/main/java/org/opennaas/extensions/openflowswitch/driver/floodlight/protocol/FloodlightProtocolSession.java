package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.FloodlightClientFactory;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class FloodlightProtocolSession implements IProtocolSession {

	public static final String						FLOODLIGHT_PROTOCOL_TYPE	= "floodlight";
	public static final String						SWITCHID_CONTEXT_PARAM_NAME	= "protocol." + FLOODLIGHT_PROTOCOL_TYPE + ".switchid";

	private ProtocolSessionContext					sessionContext				= null;
	private String									sessionId					= null;
	private Status									status						= null;

	private Map<String, IProtocolSessionListener>	protocolListeners			= null;
	private Map<String, IProtocolMessageFilter>		protocolMessageFilters		= null;

	private FloodlightClientFactory					clientFactory;
	private IFloodlightStaticFlowPusherClient		floodlightStaticFlowPusherClient;

	public FloodlightProtocolSession(String sessionID,
			ProtocolSessionContext protocolSessionContext) throws ProtocolException {
		super();
		setSessionId(sessionID);
		setSessionContext(protocolSessionContext);

		this.protocolListeners = new HashMap<String, IProtocolSessionListener>();
		this.protocolMessageFilters = new HashMap<String, IProtocolMessageFilter>();

		this.clientFactory = new FloodlightClientFactory();

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
		this.floodlightStaticFlowPusherClient = this.clientFactory.createClient((getSessionContext()));
		setStatus(Status.CONNECTED);
	}

	@Override
	public void disconnect() throws ProtocolException {
		if (!status.equals(Status.CONNECTED)) {
			throw new ProtocolException(
					"Cannot disconnect because the session is already disconnected. Current state: "
							+ status);
		}

		this.floodlightStaticFlowPusherClient = clientFactory.destroyClient();
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
	 * This method should NOT be used in Actions to retrieve the client. In Actions use getFloodlightClientForUse instead.
	 * 
	 * @return floodlightStaticFlowPusherClient
	 * @see getFloodlightClientForUse()
	 */
	public IFloodlightStaticFlowPusherClient getFloodlightClient() {
		return floodlightStaticFlowPusherClient;
	}

	public void setFloodlightClient(IFloodlightStaticFlowPusherClient floodlightStaticFlowPusherClient) {
		this.floodlightStaticFlowPusherClient = floodlightStaticFlowPusherClient;
	}

	public FloodlightClientFactory getClientFactory() {
		return clientFactory;
	}

	public void setClientFactory(FloodlightClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	/**
	 * Retrieve Client and checks session is connected. This method may be used in Actions to retrieve the client and call its methods afterwards.
	 * 
	 * @return initialized client.
	 * @throws ProtocolException
	 *             if this ProtocolSession is not connected.
	 */
	public IFloodlightStaticFlowPusherClient getFloodlightClientForUse() throws ProtocolException {
		if (!status.equals(Status.CONNECTED)) {
			throw new ProtocolException(
					"Cannot use client. Session is not connected. Current session status is " + status);
		}
		return getFloodlightClient();
	}

	protected void setStatus(Status status) {
		this.status = status;
	}

	private void checkProtocolSessionContext(ProtocolSessionContext protocolSessionContext) throws ProtocolException {

		String protocol = (String) protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL);
		if ((protocol == null) || (protocol.length() == 0) || !protocol.equals(FLOODLIGHT_PROTOCOL_TYPE)) {
			throw new ProtocolException(
					"Protocols FLOODLIGHT: Invalid protocol type: " + protocol + ". Protocol type must be " + FLOODLIGHT_PROTOCOL_TYPE);
		}

		String uri = (String) protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);
		if ((uri == null) || (uri.length() == 0)) {
			throw new ProtocolException(
					"Protocols FLOODLIGHT: Couldn't get " + ProtocolSessionContext.PROTOCOL_URI + " from protocolSessionContext.");
		}

		String switchId = (String) sessionContext.getSessionParameters().get(SWITCHID_CONTEXT_PARAM_NAME);
		if ((uri == null) || (uri.length() == 0)) {
			throw new ProtocolException(
					"Protocols FLOODLIGHT: Couldn't get " + SWITCHID_CONTEXT_PARAM_NAME + " from protocolSessionContext.");
		}

		// check given uri is a valid URI
		try {
			new URI(uri);
		} catch (URISyntaxException e) {
			throw new ProtocolException(e);
		}
	}

}
