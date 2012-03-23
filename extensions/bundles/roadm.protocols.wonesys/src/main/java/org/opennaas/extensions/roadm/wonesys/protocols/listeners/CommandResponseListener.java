package org.opennaas.extensions.roadm.wonesys.protocols.listeners;

import java.util.concurrent.CountDownLatch;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.opennaas.extensions.roadm.wonesys.transports.ITransportListener;
import org.opennaas.extensions.roadm.wonesys.transports.rawsocket.RawSocketTransport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.event.Event;

/**
 *
 * @author isart
 *
 */
public class CommandResponseListener implements ITransportListener
{
	private final static Log 		log =
		LogFactory.getLog(CommandResponseListener.class);

	private final	CountDownLatch	latch = new CountDownLatch(1);
	private final	String			commandId;
	private 		String			response;


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
						latch.countDown();
					}
				}
			}
		}
	}

	public String getResponse(long timeout)
		throws InterruptedException
	{
		latch.await(timeout, MILLISECONDS);

		return (response == null) ? null : response.toString();
	}
}
