package net.i2cat.mantychore.protocols.netconf;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import net.i2cat.nexus.protocols.sessionmanager.IProtocolMessageFilter;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionListener;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.errors.NetconfProtocolException;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.errors.TransportNotImplementedException;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.RPCElement;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetconfProtocolSession implements IProtocolSession {
	//
	public static final String						PROTOCOL_URI			= "URI";

	/** The logger **/
	Logger											log						= LoggerFactory.getLogger(NetconfProtocolSession.class);

	/**
	 * Contains information about the protocol capability configuration:
	 * transport, host, port, ...
	 **/
	private ProtocolSessionContext					protocolSessionContext	= null;
	private String									sessionID				= null;
	private Status									status					= null;
	private NetconfSession							netconfSession			= null;

	private Map<String, IProtocolSessionListener>	protocolListeners		= null;
	private Map<String, IProtocolMessageFilter>		protocolMessageFilters	= null;

	public NetconfProtocolSession(ProtocolSessionContext protocolSessionContext, String sessionID) throws ProtocolException {

		this.protocolListeners = new HashMap<String, IProtocolSessionListener>();
		this.protocolMessageFilters = new HashMap<String, IProtocolMessageFilter>();

		this.protocolSessionContext = protocolSessionContext;
		this.sessionID = sessionID;
		this.status = Status.DISCONNECTED_BY_USER;

		try {

			String uri = (String) protocolSessionContext.getSessionParameters().get("protocol.uri");
			SessionContext context = new SessionContext();
			context.setURI(new URI(uri));
			netconfSession = new NetconfSession(context);
			/* these errors can not happen */
		} catch (URISyntaxException e) {
			log.error("Error with a syntaxis");
		} catch (TransportNotImplementedException e) {
			log.error("Error with the transport not implemented");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void asyncSend(Object requestMessage) throws ProtocolException {
		// Send a message to the device, and don't wait for the response
		RPCElement rpcReply = null;
		try {
			Query rpcQuery = (Query) requestMessage;
			netconfSession.sendAsyncQuery(rpcQuery);
		} catch (Exception ex) {
			throw new ProtocolException(ex);
		}

	}

	public Object sendReceive(Object requestMessage) throws ProtocolException {
		// Send a message to the device, and wait for the response
		RPCElement rpcReply = null;
		try {
			Query rpcQuery = (Query) requestMessage;
			rpcReply = netconfSession.sendSyncQuery(rpcQuery);
		} catch (Exception ex) {
			throw new ProtocolException(ex);
		}
		return rpcReply;
	}

	public void connect() throws ProtocolException {

		if (status.equals(Status.CONNECTED)) {
			throw new ProtocolException("Cannot connect because the session is already connected");
		}
		// Start the receiver thread it will continuously get and parse the
		// messages from the communication channel
		log.info("Connecting to the device");
		try {
			netconfSession.connect();
			status = Status.CONNECTED;
			// log.info("Status " + status.toString());
		} catch (TransportException e) {
			ProtocolException te = new ProtocolException("TransportException: " + e.getMessage());
			te.initCause(e);
			throw te;
		} catch (NetconfProtocolException e) {
			ProtocolException te = new ProtocolException("NetconfProtocolException: " + e.getMessage());
			te.initCause(e);
			throw te;
		}
	}


	public void disconnect() throws ProtocolException {

		if (!status.equals(Status.CONNECTED)) {
			throw new ProtocolException("Cannot disconnect because the session is already disconnected. Current state: " + status);
		}
		try {
			netconfSession.disconnect();
			status = Status.DISCONNECTED_BY_USER;
		} catch (TransportException e) {
			throw new ProtocolException(e);
		}
		log.info("Protocol session stopped");
	}

	public ProtocolSessionContext getSessionContext() {

		return protocolSessionContext;
	}


	public String getSessionID() {
		return sessionID;
	}


	public Status getStatus() {

		return status;
	}


	public void registerProtocolSessionListener(IProtocolSessionListener protocolSessionListener, IProtocolMessageFilter protocolMessageFilter,
			String idListener) {
		protocolMessageFilters.put(idListener, protocolMessageFilter);
		protocolListeners.put(idListener, protocolSessionListener);

	}


	public void unregisterProtocolSessionListener(IProtocolSessionListener protocolSessionListener, String idListener) {
		protocolMessageFilters.remove(idListener);
		protocolListeners.remove(idListener);

	}

}