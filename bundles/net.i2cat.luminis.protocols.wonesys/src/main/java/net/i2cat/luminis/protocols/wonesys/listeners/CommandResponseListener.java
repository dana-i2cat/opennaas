package net.i2cat.luminis.protocols.wonesys.listeners;

import net.i2cat.luminis.transports.wonesys.ITransportListener;
import net.i2cat.luminis.transports.wonesys.rawsocket.RawSocketTransport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.event.Event;

/**
 * 
 * @author isart
 * 
 */
public class CommandResponseListener extends Thread implements ITransportListener {

	Log						log			= LogFactory.getLog(CommandResponseListener.class);

	private String			commandId;
	private String			response	= null;

	private final Object	lock		= new Object();

	boolean					shouldStop	= false;

	public CommandResponseListener(String sentMessage) {
		commandId = getCommandId(sentMessage);
	}

	private String getCommandId(String command) {
		return command.substring(12, 16);
	}

	/**
	 * Command layout: Header (2B) | DeviceID (2B) | Reserved (2B) | CommandID (2B) | Reserved (4B) | DataLength (2B) | Data | XOR (1B) | EOS (1B)
	 * 
	 * @param response
	 * @return
	 */
	private boolean isCommand(String response) {
		return response.startsWith("5910") && response.substring(8, 12).equalsIgnoreCase("ffff") && response.substring(16, 24).equalsIgnoreCase(
				"ffffffff");

	}

	/**
	 * Errors are commands with CommandID's first byte = 0x0C
	 * 
	 * @param response
	 * @return
	 */
	private boolean isError(String response) {
		return getCommandId(response).substring(0, 2).equalsIgnoreCase("0c");
	}

	@Override
	public void handleEvent(Event event) {
		if (event.getTopic().equals(RawSocketTransport.MSG_RCVD_EVENT_TOPIC)) {
			String message = (String) event.getProperty(RawSocketTransport.MESSAGE_PROPERTY_NAME);
			if (message != null) {

				log.info("CommandResponseListener received a message: " + message);
				// check message is a response to sentMessage
				if (isCommand(message)) {
					// FIXME assuming error is a response to the command we are waiting for (not to any other)
					if (getCommandId(message).equalsIgnoreCase(commandId) || isError(message)) {
						log.info("Response received: " + message);
						response = message;
						synchronized (this) {
							notifyAll();
						}
					}
				}
			}
		}
	}

	@Override
	public void run() {

		while (!shouldStop) {
			waitForEvents();
		}

	}

	public void waitForEvents() {
		try {
			synchronized (this) {
				wait();
			}
		} catch (InterruptedException e) {
			// nothing to do, just stop waiting
		}
	}

	public void cancel() {
		shouldStop = true;
	}

	public String getResponse() {
		if (response == null)
			return null;

		return response.toString();
	}

}
