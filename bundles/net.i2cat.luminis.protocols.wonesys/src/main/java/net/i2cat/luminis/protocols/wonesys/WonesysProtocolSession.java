package net.i2cat.luminis.protocols.wonesys;

import java.util.HashMap;
import java.util.Map;

import net.i2cat.luminis.transports.wonesys.ITransport;
import net.i2cat.luminis.transports.wonesys.MockTransport;
import net.i2cat.luminis.transports.wonesys.WonesysTransport;
import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WonesysProtocolSession implements IProtocolSession {
	//
	public static final String						PROTOCOL_URI			= "URI";

	/** The logger **/
	Log												log						= LogFactory.getLog(WonesysProtocolSession.class);

	/**
	 * Contains information about the protocol capability configuration: transport, host, port, ...
	 **/
	private ProtocolSessionContext					protocolSessionContext	= null;
	private String									sessionID				= null;
	private Status									status					= null;
	private ITransport								wonesysTransport		= null;

	private Map<String, IProtocolSessionListener>	protocolListeners		= null;
	private Map<String, IProtocolMessageFilter>		protocolMessageFilters	= null;

	public WonesysProtocolSession(ProtocolSessionContext protocolSessionContext, String sessionID) throws ProtocolException {

		this.protocolListeners = new HashMap<String, IProtocolSessionListener>();
		this.protocolMessageFilters = new HashMap<String, IProtocolMessageFilter>();

		this.protocolSessionContext = protocolSessionContext;
		this.sessionID = sessionID;
		this.status = Status.DISCONNECTED_BY_USER;
		/* is mock or not */
		String isMock = (String) protocolSessionContext.getSessionParameters().get("protocol.mock");
		if (isMock != null && isMock.equals("true"))
			wonesysTransport = new MockTransport();
		else
			wonesysTransport = new WonesysTransport(protocolSessionContext);
	}

	@Override
	public void asyncSend(Object requestMessage) throws ProtocolException {
		// Send a message to the device, and don't wait for the response
		throw new ProtocolException("Unsupported Operation");
	}

	@Override
	public Object sendReceive(Object requestMessage) throws ProtocolException {
		// Send a message to the device, and wait for the response
		String reply = null;
		try {
			String message = (String) requestMessage;
			reply = (String) wonesysTransport.sendMsg(message);
		} catch (Exception e) {
			throw new ProtocolException("TransportException: " + e.getMessage());
		}
		return reply;
	}

	@Override
	public void connect() throws ProtocolException {

		if (status.equals(Status.CONNECTED)) {
			throw new ProtocolException("Cannot connect because the session is already connected");
		}

		log.info("Connecting to the device");
		try {
			wonesysTransport.connect();
			changeSessionStatus(Status.CONNECTED);
		} catch (Exception e) {
			ProtocolException pe = new ProtocolException("TransportException: " + e.getMessage());
			// te.initCause(e);
			throw pe;
		}
	}

	@Override
	public void disconnect() throws ProtocolException {

		if (!status.equals(Status.CONNECTED)) {
			throw new ProtocolException("Cannot disconnect because the session is not connected. Current state: " + status);
		}
		try {
			wonesysTransport.disconnect();
			changeSessionStatus(Status.DISCONNECTED_BY_USER);
		} catch (Exception e) {
			throw new ProtocolException("TransportException: " + e.getMessage());
		}
		log.info("Protocol session stopped");
	}

	@Override
	public ProtocolSessionContext getSessionContext() {
		return protocolSessionContext;
	}

	@Override
	public void setSessionContext(ProtocolSessionContext context) {
		// FIXME should create a new Transport ????
		this.protocolSessionContext = context;
	}

	@Override
	public String getSessionId() {
		return sessionID;
	}

	@Override
	public void setSessionId(String sessionId) {
		this.sessionID = sessionId;
	}

	@Override
	public Status getStatus() {

		return status;
	}

	@Override
	public void registerProtocolSessionListener(IProtocolSessionListener protocolSessionListener, IProtocolMessageFilter protocolMessageFilter,
			String idListener) {
		// protocolMessageFilters.put(idListener, protocolMessageFilter);
		// protocolListeners.put(idListener, protocolSessionListener);

	}

	@Override
	public void unregisterProtocolSessionListener(IProtocolSessionListener protocolSessionListener, String idListener) {
		// protocolMessageFilters.remove(idListener);
		// protocolListeners.remove(idListener);

	}

	public void changeSessionStatus(Status status) {
		this.status = status;
		// notifyListeners();

	}

}