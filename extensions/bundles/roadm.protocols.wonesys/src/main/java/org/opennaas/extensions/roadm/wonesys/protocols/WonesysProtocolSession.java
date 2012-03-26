package org.opennaas.extensions.roadm.wonesys.protocols;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.opennaas.extensions.roadm.wonesys.protocols.listeners.CommandResponseListener;
import org.opennaas.extensions.roadm.wonesys.protocols.listeners.RawSocketAlarmListener;
import org.opennaas.extensions.roadm.wonesys.transports.ITransport;
import org.opennaas.extensions.roadm.wonesys.transports.ITransportListener;
import org.opennaas.extensions.roadm.wonesys.transports.WonesysTransport;
import org.opennaas.extensions.roadm.wonesys.transports.mock.MockTransport;
import org.opennaas.extensions.roadm.wonesys.transports.rawsocket.RawSocketTransport;
import static org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolBundleActivator.getEventManagerService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class WonesysProtocolSession implements IProtocolSession, ITransportListener {
	//
	public static final String						PROTOCOL_URI				= "URI";
	public static final long						PROTOCOL_DEFAULT_TIMEOUT	= 30000;															// millis

	public static final String						CMD_RCVD_EVENT_TOPIC		= "net/i2cat/luminis/protocol/wonesys/session/COMMAND_RECEIVED";
	public static final String						ALARM_RCVD_EVENT_TOPIC		= "net/i2cat/luminis/protocol/wonesys/session/alarms/RECEIVED";

	public static final String						COMMAND_PROPERTY_NAME		= "command";
	public static final String						SESSION_ID_PROPERTY			= "sessionId";

	/** The logger **/
	public static Log								log							= LogFactory.getLog(WonesysProtocolSession.class);

	/**
	 * Contains information about the protocol capability configuration: transport, host, port, ...
	 **/
	private ProtocolSessionContext					protocolSessionContext		= null;
	private String									sessionID					= null;
	private Status									status						= null;
	private ITransport								wonesysTransport			= null;

	private Map<String, IProtocolSessionListener>	protocolListeners			= null;
	private Map<String, IProtocolMessageFilter>		protocolMessageFilters		= null;

	private final Object							mutex						= new Object();
	private Object									response;
	private long									timeout;

	RawSocketAlarmListener							rawSocketAlarmListener;

	public WonesysProtocolSession(ProtocolSessionContext protocolSessionContext, String sessionID) throws ProtocolException {

		this.protocolListeners = new HashMap<String, IProtocolSessionListener>();
		this.protocolMessageFilters = new HashMap<String, IProtocolMessageFilter>();

		this.protocolSessionContext = protocolSessionContext;
		this.sessionID = sessionID;
		this.status = Status.DISCONNECTED_BY_USER;

		log.debug("Initializing transport");
		/* is mock or not */
		if (WonesysProtocolSessionContextUtils.isMock(protocolSessionContext))
			wonesysTransport = new MockTransport();
		else
			wonesysTransport = new WonesysTransport(protocolSessionContext);

		String timeoutParam = (String) protocolSessionContext.getSessionParameters().get("protocol.responsetimeout");
		if (timeoutParam != null)
			timeout = Long.parseLong(timeoutParam);
		else
			timeout = PROTOCOL_DEFAULT_TIMEOUT;

		// register session as a transport listener
		// this session will receive all events from its wonesysTransport
		log.info("Registering session as a transport listener");
		registerToTransport(this, RawSocketTransport.ALL_EVENTS_TOPIC);

		log.info("Registering alarm listener");
		rawSocketAlarmListener = new RawSocketAlarmListener(sessionID);
		registerToTransport(rawSocketAlarmListener, RawSocketTransport.MSG_RCVD_EVENT_TOPIC);
	}

	@Override
	synchronized public void asyncSend(Object requestMessage) throws ProtocolException {
		// Send a message to the device, and don't wait for the response
		throw new ProtocolException("Unsupported Operation");
	}

	/**
	 * Send a message to the device, and wait for the response.
	 */
	@Override
	synchronized public Object sendReceive(Object requestMessage)
		throws ProtocolException
	{
		try {
			String message = (String) requestMessage;
			CommandResponseListener responseListener =
				new CommandResponseListener(message);
			int serviceId =
				registerToTransport(responseListener,
									RawSocketTransport.MSG_RCVD_EVENT_TOPIC);
			try {
				wonesysTransport.sendMsg(message);
				log.info("Message sent");
				return waitResponse(responseListener);
			} finally {
				getEventManagerService().unregisterHandler(serviceId);
			}
		} catch (ProtocolException e) {
			throw e;
		} catch (Exception e) {
			throw new ProtocolException("TransportException: ", e);
		}
	}

	private int registerToTransport(EventHandler listener, String topic)
		throws ProtocolException
	{
		try {
			Properties properties = new Properties();
			properties.setProperty(RawSocketTransport.TRANSPORT_ID_PROPERTY_NAME, wonesysTransport.getTransportID());
			EventFilter filter = new EventFilter(new String[] { topic }, properties);

			IEventManager eventManager = getEventManagerService();
			return eventManager.registerEventHandler(listener, filter);
		} catch (ActivatorException e) {
			throw new ProtocolException("Failed to register to transport events.", e);
		}
	}

	private String waitResponse(CommandResponseListener responseListener)
		throws ProtocolException
	{
		try {
			long start = System.currentTimeMillis();
			log.debug("Waiting for a response within " + timeout + "ms @ " + start);
			String response = responseListener.getResponse(timeout);
			log.debug("Finished waiting. Waited during " + (System.currentTimeMillis() - start) + "ms");

			if (response == null) {
				throw new ProtocolException("Timeout waiting for a command response");
			}

			return response;
		} catch (InterruptedException e) {
			throw new ProtocolException("Error while receiving message response: " + e.getMessage(), e);
		}
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

	// FIXME allow sessionListeners
	@Override
	public void registerProtocolSessionListener(IProtocolSessionListener protocolSessionListener, IProtocolMessageFilter protocolMessageFilter,
			String idListener) {
		protocolMessageFilters.put(idListener, protocolMessageFilter);
		protocolListeners.put(idListener, protocolSessionListener);

	}

	@Override
	public void unregisterProtocolSessionListener(IProtocolSessionListener protocolSessionListener, String idListener) {
		protocolMessageFilters.remove(idListener);
		protocolListeners.remove(idListener);

	}

	public void changeSessionStatus(Status status) {
		this.status = status;
		// notifyListeners();

	}

	public ITransport getWonesysTransport() {
		return wonesysTransport;
	}

	@Override
	/**
	 * This method manages events. This method is responsible to manage two type of alarms, ERROR_EVENT and CMD_RCVD. Other type of alarms are managed for
	 * RAWSocketAlarmListener
	 */
	public void handleEvent(Event event) {
		log.debug("Event received");
		if (event.getTopic().equals(RawSocketTransport.ERROR_EVENT_TOPIC)) {
			Exception error = (Exception) event.getProperty(RawSocketTransport.ERROR_PROPERTY_NAME);
			if (error != null) {
				errorHappened(error);
			}
		} else if (event.getTopic().equals(CMD_RCVD_EVENT_TOPIC)) {
			String response = (String) event.getProperty(COMMAND_PROPERTY_NAME);
			if (response != null) {
				commandReceived(response);
			}
		}
	}

	public void commandReceived(Object message) {
		log.info("Message received by session");
		synchronized (mutex) {
			response = message;
			mutex.notify();
		}
	}

	public void errorHappened(Exception e) {
		log.error("Error in session " + sessionID, e);
		log.info("Disconnecting session " + sessionID);
		// disconnect session
		try {
			disconnect();
		} catch (ProtocolException e1) {
			log.error("Failed to disconnect session. This error may create a memory leak due to unclosed sockets", e1);
		}

		changeSessionStatus(Status.CONNECTION_LOST);
		// FIXME notify listeners

	}

}