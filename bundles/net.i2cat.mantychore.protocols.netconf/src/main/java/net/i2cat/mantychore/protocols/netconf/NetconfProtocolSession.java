package net.i2cat.mantychore.protocols.netconf;

import java.net.URI;
import java.net.URISyntaxException;

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

import com.iaasframework.protocolsessionmanager.IProtocolMessageFilter;
import com.iaasframework.protocolsessionmanager.IProtocolSession;
import com.iaasframework.protocolsessionmanager.IProtocolSessionListener;
import com.iaasframework.protocolsessionmanager.ProtocolException;
import com.iaasframework.protocolsessionmanager.ProtocolSessionContext;

public class NetconfProtocolSession implements IProtocolSession {
	//
	public static final String		PROTOCOL_URI			= "URI";
	// public static final String fileProperties = "netconf-default.properties";

	// NetconfSession netconfSession = null;

	// /** The logger **/
	// Logger log = LoggerFactory.getLogger(NetconfProtocolSession.class);

	// public NetconfProtocolSession(CapabilityDescriptor capabilityDescriptor) {
	//
	// try {
	// String uri = capabilityDescriptor.getPropertyValue(PROTOCOL_URI);
	// SessionContext context = new SessionContext();
	// context.setURI(new URI(uri));
	//
	// netconfSession = new NetconfSession(context);
	// // netconfSession.loadConfiguration(new
	// // PropertiesConfiguration(fileProperties));
	// /* these errors can not happen */
	// } catch (URISyntaxException e) {
	// log.error("Error with a syntaxis");
	// } catch (TransportNotImplementedException e) {
	// log.error("Error with the transport not implemented");
	// } catch (ConfigurationException e) {
	// e.printStackTrace();
	// }
	// }

	// public void wireTransport(ITransport arg0) throws ProtocolException {
	// // the transport is hardcoded with the netconf library which we will use
	// }

	// public void run() {
	// try {
	// startSession();
	//
	// while (!isDisposed()) {
	// try {
	//
	// ProtocolRequestMessage currentProtocolMessage = incomingRequestsQueue.take();
	// if (!currentProtocolMessage.isLastMessage()) {
	// try {
	// // send message and wait a response
	//
	// // FIXME The request Message, it should be a Object
	// // not a String. It must be serialized...
	// Object objReqMessage = SerializerHelper.stringToObject(currentProtocolMessage.getMessage());
	//
	// Object deviceResponse = sendWaitResponse(objReqMessage);
	//
	// // send response to our protocol module
	// sendResponseToProtocolModule(deviceResponse, currentProtocolMessage.getMessageID());
	// } catch (ProtocolException ex) {
	// sendErrorToProtocolModule(ex,
	// currentProtocolMessage.getMessageID());
	// }
	// } else {
	// this.setDisposed(true);
	// }
	// } catch (InterruptedException ex) {
	// ex.printStackTrace();
	// }
	// }
	//
	// stopSession();
	// } catch (ProtocolException e) {
	// log.error("It was impossible to manage the session", e.getMessage(), e);
	// }
	//
	// }

	// private void startSession() throws ProtocolException {
	// try {
	// netconfSession.connect();
	// } catch (TransportException e) {
	// new ProtocolException(e);
	// } catch (NetconfProtocolException e) {
	// new ProtocolException(e);
	// }
	// }
	//
	// private void stopSession() throws ProtocolException {
	// try {
	// netconfSession.disconnect();
	// } catch (TransportException e) {
	// throw new ProtocolException(e);
	// }
	//
	// }

	// private Object sendWaitResponse(Object requestMessage) throws ProtocolException {
	// RPCElement rpcReply = null;
	// try {
	// Query rpcQuery = (Query) requestMessage;
	// rpcReply = netconfSession.sendSyncQuery(rpcQuery);
	// } catch (Exception ex) {
	// throw new ProtocolException(ex);
	// }
	// return rpcReply;
	// }
	//
	// private void sendResponseToProtocolModule(Object responseMessage, String correlation) {
	// ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage();
	// protocolResponseMessage.setProtocolMessage(responseMessage);
	// protocolCapability.sendSessionResponse(protocolResponseMessage, correlation);
	// }
	//
	// private void sendErrorToProtocolModule(ProtocolException ex, String messageID) {
	// ProtocolErrorMessage protocolErrorMessage = new ProtocolErrorMessage();
	// protocolErrorMessage.setException(ex);
	// protocolCapability.sendSessionErrorResponse(protocolErrorMessage, messageID);
	//
	// }

	/** The logger **/
	Logger							log						= LoggerFactory.getLogger(NetconfProtocolSession.class);

	/** Contains information about the protocol capability configuration: transport, host, port, ... **/
	// This was the CapabilityDescriptor class
	private ProtocolSessionContext	protocolSessionContext	= null;
	private String					sessionID				= null;
	private Status					status					= null;
	private NetconfSession			netconfSession			= null;

	public NetconfProtocolSession(ProtocolSessionContext protocolSessionContext, String sessionID) throws ProtocolException {

		this.protocolSessionContext = protocolSessionContext;
		this.sessionID = sessionID;
		this.status = Status.DISCONNECTED_BY_USER;

		try {

			String uri = (String) protocolSessionContext.getSessionParameters().get("protocol.uri");
			SessionContext context = new SessionContext();
			context.setURI(new URI(uri));
			netconfSession = new NetconfSession(context);
			// log.info("netconfSession created");
			// netconfSession.loadConfiguration(new
			// PropertiesConfiguration(fileProperties));
			/* these errors can not happen */
		} catch (URISyntaxException e) {
			log.error("Error with a syntaxis");
		} catch (TransportNotImplementedException e) {
			log.error("Error with the transport not implemented");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void asyncSend(Object requestMessage) throws ProtocolException {
		// TODO Auto-generated method stub
		// Send a message to the device, and don't wait for the response
		RPCElement rpcReply = null;
		try {
			Query rpcQuery = (Query) requestMessage;
			netconfSession.sendAsyncQuery(rpcQuery);
		} catch (Exception ex) {
			throw new ProtocolException(ex);
		}

	}

	@Override
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

	@Override
	public void connect() throws ProtocolException {

		if (status.equals(Status.CONNECTED)) {
			throw new ProtocolException("Cannot connect because the session is already connected");
		}
		// Start the receiver thread it will continuously get and parse the messages from the communication channel
		log.info("Connecting to the device");
		try {
			netconfSession.connect();
			status = Status.CONNECTED;
			// log.info("Status " + status.toString());
		} catch (TransportException e) {
			new ProtocolException("Could not connect to the managed device", e);
		} catch (NetconfProtocolException e) {
			new ProtocolException(e);
		}
	}

	@Override
	public void disconnect() throws ProtocolException {

		if (!status.equals(Status.CONNECTED)) {
			throw new ProtocolException("Cannot disconnect because the session is already disconnected. Current state: " + status);
		}
		try {
			netconfSession.disconnect();
			status.equals(Status.DISCONNECTED_BY_USER);
		} catch (TransportException e) {
			throw new ProtocolException(e);
		}
		log.info("Protocol session stopped");
	}

	@Override
	public ProtocolSessionContext getSessionContext() {

		return protocolSessionContext;
	}

	@Override
	public String getSessionID() {
		return sessionID;
	}

	@Override
	public Status getStatus() {

		return status;
	}

	@Override
	public void registerProtocolSessionListener(IProtocolSessionListener arg0, IProtocolMessageFilter arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterProtocolSessionListener(IProtocolSessionListener arg0) {
		// TODO Auto-generated method stub

	}

}