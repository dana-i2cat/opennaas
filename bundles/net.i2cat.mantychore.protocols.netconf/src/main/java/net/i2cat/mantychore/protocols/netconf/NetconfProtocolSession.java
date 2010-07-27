package net.i2cat.mantychore.protocols.netconf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.protocol.AbstractProtocolSession;
import com.iaasframework.capabilities.protocol.IProtocolConstants;
import com.iaasframework.capabilities.protocol.ProtocolErrorMessage;
import com.iaasframework.capabilities.protocol.ProtocolException;
import com.iaasframework.capabilities.protocol.ProtocolRequestMessage;
import com.iaasframework.capabilities.protocol.ProtocolResponseMessage;
import com.iaasframework.core.transports.IStreamTransport;
import com.iaasframework.core.transports.ITransport;
import com.iaasframework.core.transports.TransportException;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.descriptor.CapabilityProperty;

import net.i2cat.mantychore.protocols.netconf.message.NetconfInputMessage;
import net.i2cat.mantychore.protocols.netconf.message.NetconfResponseMessage;

/**
 * This class represents a CLI Session. When it is created, it logs in the
 * remote agent. Then it sends messages to the agent, parses its responses and
 * sends them back to the CLI Session client. A timer task wakes up every 90
 * seconds and sends a message specified by the client when initializing the
 * session (or a dummy message if no message is specified)
 * 
 * @author Eduard Grasa
 * 
 */
public class NetconfProtocolSession extends AbstractProtocolSession {

	public static final String			MODULE_NAME					= "CLI Protocol Session";
	public static final String			MODULE_DESCRIPTION			= "CLI Protocol library";
	public static final String			MODULE_VERSION				= "1.0.0";

	/** CLI Session Log */
	static private Logger				logger						= LoggerFactory.getLogger(NetconfProtocolSession.class);

	/** Some constants **/
	public static final String			HAS_GREETING				= "Greeting";												// CLI
																																// has
																																// to
																																// wait
																																// for
																																// a
																																// greeting
																																// message
																																// from
																																// transport
	public static final String			ENABLE_COMMAND				= "enableCommand";
	public static final String			ENABLE_USERNAME				= "enableUsername";
	public static final String			ENABLE_PASSWORD				= "enablePassword";
	public static final String			PROMPT						= "prompt";
	public static final String			INTERMEDIATE_PROMPT			= "intermediatePrompt";
	public static final String			INTERMEDIATE_PROMPT_COMMAND	= "intermediatePromptCommand";
	public static final String			ERROR_MESSAGE_PATTERNS		= "errorMessagePatterns";
	public static final String			KEEP_ALIVE_MESSAGE			= "keepAliveMessage";

	/** The prompts used to detect the end of the message **/
	private List<String>				prompts;

	/**
	 * Contains a pair of <intermediate_prompt, string to be send>, to make the
	 * remote agent continue sending the response message
	 **/
	private Hashtable<String, String>	intermediatePrompts;

	/**
	 * Contains some of the strings that appear when the response message is an
	 * error message
	 **/
	private List<String>				errorMessagePatterns;

	/** The class that will listen for response messages **/
	private NetconfStreamReader			cliStreamReader;

	/**
	 * Contains information about the protocol capability configuration:
	 * transport, host, port, ...
	 **/
	private CapabilityDescriptor		capabilityDescriptor;

	/** the transport to communicate with the managed entity **/
	private IStreamTransport			transport;

	private ProtocolRequestMessage		currentProtocolMessage		= null;

	public NetconfProtocolSession(CapabilityDescriptor capabilityDescriptor) throws ProtocolException {
		this.capabilityDescriptor = capabilityDescriptor;
		setPromptsAndErrorPatterns(capabilityDescriptor.getCapabilityProperties());
	}

	private void setPromptsAndErrorPatterns(List<CapabilityProperty> capabilityProperties) throws ProtocolException {
		initializePromptsAndErrorPatterns();
		extractPromptsAndErrorPatternsFromModuleProperties(capabilityProperties);
		checkMissingParameters();
	}

	private void initializePromptsAndErrorPatterns() {
		prompts = new ArrayList<String>();
		intermediatePrompts = new Hashtable<String, String>();
		errorMessagePatterns = new ArrayList<String>();
	}

	private void extractPromptsAndErrorPatternsFromModuleProperties(List<CapabilityProperty> capabilityProperties) {
		CapabilityProperty currentProperty = null;
		CapabilityProperty nextProperty = null;
		for (int i = 0; i < capabilityProperties.size(); i++) {
			currentProperty = capabilityProperties.get(i);
			if (currentProperty.getName().equals(PROMPT)) {
				prompts.add(currentProperty.getValue());
			} else if (currentProperty.getName().equals(INTERMEDIATE_PROMPT)) {
				i++;
				nextProperty = capabilityProperties.get(i);
				if (currentProperty.getName().equals(INTERMEDIATE_PROMPT_COMMAND)) {
					intermediatePrompts.put(currentProperty.getValue(), nextProperty.getValue());
				}
			} else if (currentProperty.getName().equals(ERROR_MESSAGE_PATTERNS)) {
				errorMessagePatterns.add(currentProperty.getValue());
			}
		}
	}

	private void checkMissingParameters() throws ProtocolException {
		// Check there is enough info to segment the messages and identify
		// errors
		if (prompts.size() == 0) {
			throw new ProtocolException("No prompts received. CLI Session needs to know the prompt strings in order to " +
					"segment the response messages.");
		}

		if (errorMessagePatterns.size() == 0) {
			throw new ProtocolException("No error message patterns received. CLI Session needs to know the error messages strings " +
					"in order to identify failed request messages.");
		}

		if (intermediatePrompts.isEmpty()) {
			logger.warn("No intermediate prompts received, strange.");
		}
	}

	public void wireTransport(ITransport transport) throws ProtocolException {
		if (!(transport instanceof IStreamTransport)) {
			throw new ProtocolException("CLI transports must be stream transports");
		}

		this.transport = (IStreamTransport) transport;
	}

	public void run() {
		try {
			startSession();

			while (!isDisposed()) {
				try {
					currentProtocolMessage = incomingRequestsQueue.take();
					if (!currentProtocolMessage.isLastMessage()) {
						try {
							Object deviceResponse = sendWaitResponse(currentProtocolMessage.getMessage());
							sendResponseToProtocolCapability(deviceResponse, currentProtocolMessage.getMessageID());
						} catch (ProtocolException ex) {
							sendErrorResponseToProtocolCapability(ex, currentProtocolMessage.getMessageID());
						}
					}
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}

			stopSession();
		} catch (ProtocolException ex) {
			ex.printStackTrace();
			// TODO tell the protocol engineModule we're in trouble
		}
	}

	/**
	 * Opens a connection to the specific host/port
	 */
	private void startSession() throws ProtocolException {
		logger.info("Starting protocol session");
		try {
			transport.connect();
		} catch (TransportException ex) {
			throw new ProtocolException("Problems connecting to the managed device", ex);
		}
		createCLIStreamReader();
		loginToDevice(capabilityDescriptor);
	}

	public void restartSession() {
		if (!isDisposed()) {
			try {
				stopSession();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				transport.connect();
				loginToDevice(capabilityDescriptor);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void sendResponseToProtocolCapability(Object responseMessage, String correlation) {
		ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage();
		protocolResponseMessage.setProtocolMessage(responseMessage);
		protocolCapability.sendSessionResponse(protocolResponseMessage, correlation);
	}

	private void sendErrorResponseToProtocolCapability(ProtocolException protocolException, String correlation) {
		ProtocolErrorMessage protocolErrorMessage = new ProtocolErrorMessage();
		protocolErrorMessage.setException(protocolException);
		protocolCapability.sendSessionErrorResponse(protocolErrorMessage, correlation);
	}

	private void createCLIStreamReader() throws ProtocolException {
		try {
			cliStreamReader = new NetconfStreamReader(getTransport().getInputStream(), getTransport().getOutputStream(),
					prompts, intermediatePrompts, errorMessagePatterns);
		} catch (TransportException ex) {
			throw new ProtocolException("Problems getting the transport input stream", ex);
		}
	}

	private IStreamTransport getTransport() {
		return transport;
	}

	private void loginToDevice(CapabilityDescriptor capabilityDescriptor) throws ProtocolException {
		if (capabilityDescriptor.getProperty(NetconfProtocolSession.HAS_GREETING) != null) {
			try {
				cliStreamReader.getResponse("");// wait for greeting message
			} catch (IOException e) {
				e.printStackTrace();
				throw new ProtocolException("CLI cannot read greeting message: " + e.toString());
			}
		}
		if (capabilityDescriptor.getProperty(IProtocolConstants.PROTOCOL_USERNAME) != null) {
			String username = capabilityDescriptor.getProperty(IProtocolConstants.PROTOCOL_USERNAME).getValue();
			sendWaitResponse(username);
		}

		if (capabilityDescriptor.getProperty(IProtocolConstants.PROTOCOL_PASSWORD) != null) {
			String password = capabilityDescriptor.getProperty(IProtocolConstants.PROTOCOL_PASSWORD).getValue();
			sendWaitResponse(password);
		}
	}

	/**
	 * Stop the protocol session: log out, stop the session keep-alive thread,
	 * disconnect the transport
	 */
	public void stopSession() throws ProtocolException {
		try {
			getTransport().disconnect();
		} catch (TransportException ex) {
			throw new ProtocolException("Problems disconnecting the transport", ex);
		}
	}

	/**
	 * Send a message without waiting for the response
	 */
	public synchronized void sendDontWaitResponse(Object message) throws ProtocolException {
		if (message instanceof NetconfInputMessage) {
			NetconfInputMessage request = (NetconfInputMessage) message;
			this.sendCmdNoWait(request.toString());
		} else {
			this.sendCmdNoWait((String) message);
		}
	}

	/**
	 * Sends an array of messages without waiting for the response
	 */
	public synchronized void sendDontWaitResponse(Object[] messageList) throws ProtocolException {
		for (int i = 0; i < messageList.length; i++) {
			sendDontWaitResponse(messageList[i]);
		}
	}

	/**
	 * Sends out CLI command to the agent.
	 * 
	 * @param message
	 *            Command to send in CLIInputMessage Format
	 * @return CLIResponseMesssage
	 * @throws FailedCmdException
	 *             Exception thrown if command failed
	 */
	public synchronized Object sendWaitResponse(Object message) throws ProtocolException {
		Object msg;
		if (message instanceof NetconfInputMessage) {
			NetconfInputMessage request = (NetconfInputMessage) message;
			msg = (Object) this.sendCmdWait(request.toString());
		} else {
			msg = (Object) this.sendCmdWait((String) message);
		}
		return msg;
	}

	/**
	 * Sends an array of CLI commands to the device
	 */
	public synchronized Object[] sendWaitResponse(Object[] messageList) throws ProtocolException {
		Object[] msg = new Object[messageList.length];
		for (int i = 0; i < messageList.length; i++) {
			msg[i] = sendWaitResponse(messageList[i]);
		}
		return msg;
	}

	/**
	 * Sends a CLI message and waits for the response
	 * 
	 * @param message
	 * @return
	 */
	private synchronized NetconfResponseMessage sendCmdWait(String message) throws ProtocolException {
		// Send the message
		this.sendCmdNoWait(message);
		// Get the response
		try {
			return cliStreamReader.getResponse(message);
		} catch (IOException e) {
			e.printStackTrace();
			restartSession();
			return this.sendCmdWait(message);
		}
	}

	/**
	 * Sends a command to the stream transport, without waiting for the request
	 * 
	 * @param request
	 * @throws ProtocolException
	 */
	private void sendCmdNoWait(String request) throws ProtocolException {
		try {
			getTransport().send(request.toCharArray());
		} catch (TransportException ex) {
			throw new ProtocolException("Problems sending this message to the managed device: " + request, ex);
		}
	}
}