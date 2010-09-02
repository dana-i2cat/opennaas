package net.i2cat.mantychore.protocols.netconf;

import java.net.URI;
import java.net.URISyntaxException;

import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.errors.NetconfProtocolException;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.errors.TransportNotImplementedException;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.RPCElement;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.protocol.AbstractProtocolSession;
import com.iaasframework.capabilities.protocol.ProtocolException;
import com.iaasframework.capabilities.protocol.api.ProtocolErrorMessage;
import com.iaasframework.capabilities.protocol.api.ProtocolRequestMessage;
import com.iaasframework.capabilities.protocol.api.ProtocolResponseMessage;
import com.iaasframework.core.transports.ITransport;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;

public class NetconfProtocolSession extends AbstractProtocolSession {
	public static final String	PROTOCOL_URI	= "URI";
	public static final String	fileProperties	= "netconf-default.properties";
	NetconfSession				netconfSession	= null;

	/** The logger **/
	Logger						log				= LoggerFactory.getLogger(NetconfProtocolSession.class);

	public NetconfProtocolSession(CapabilityDescriptor capabilityDescriptor) {

		try {
			// TODO put identifier in a constant project
			String uri = capabilityDescriptor.getPropertyValue(PROTOCOL_URI);
			netconfSession = new NetconfSession(new URI(uri));
			netconfSession.loadConfiguration(new
					PropertiesConfiguration(fileProperties));

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
	public void wireTransport(ITransport arg0) throws ProtocolException {
		// the transport is hardcoded with the netconf library which we will use
	}

	@Override
	public void run() {
		try {
			startSession();

			while (!isDisposed()) {
				try {

					ProtocolRequestMessage currentProtocolMessage = incomingRequestsQueue.take();
					if (!currentProtocolMessage.isLastMessage()) {
						try {
							// send message and wait a response
							Object deviceResponse = sendWaitResponse(currentProtocolMessage.getMessage());

							// send response to our protocol module
							sendResponseToProtocolModule(deviceResponse, currentProtocolMessage.getMessageID());
						} catch (ProtocolException ex) {
							sendErrorToProtocolModule(ex,
									currentProtocolMessage.getMessageID());
						}
					} else {
						this.setDisposed(true);
					}
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}

			stopSession();
		} catch (ProtocolException e) {
			log.error("It was impossible to manage the session", e.getMessage(), e);
		}

	}

	private void startSession() throws ProtocolException {
		try {
			netconfSession.connect();
		} catch (TransportException e) {
			new ProtocolException(e);
		} catch (NetconfProtocolException e) {
			new ProtocolException(e);
		}
	}

	private void stopSession() throws ProtocolException {
		try {
			netconfSession.disconnect();
		} catch (TransportException e) {
			throw new ProtocolException(e);
		}

	}

	private Object sendWaitResponse(Object requestMessage) throws ProtocolException {
		RPCElement rpcReply = null;
		try {
			Query rpcQuery = (Query) requestMessage;
			rpcReply = netconfSession.sendSyncQuery(rpcQuery);
		} catch (Exception ex) {
			throw new ProtocolException(ex);
		}
		return rpcReply;
	}

	private void sendResponseToProtocolModule(Object responseMessage, String correlation) {
		ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage();
		protocolResponseMessage.setProtocolMessage(responseMessage);
		protocolCapability.sendSessionResponse(protocolResponseMessage, correlation);
	}

	private void sendErrorToProtocolModule(ProtocolException ex, String messageID) {
		ProtocolErrorMessage protocolErrorMessage = new ProtocolErrorMessage();
		protocolErrorMessage.setException(ex);
		protocolCapability.sendSessionErrorResponse(protocolErrorMessage, messageID);

	}

}