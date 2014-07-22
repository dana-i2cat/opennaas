package org.opennaas.extensions.roadm.wonesys.protocols.listeners;

/*
 * #%L
 * OpenNaaS :: ROADM :: W-Onesys Protocol
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.extensions.roadm.wonesys.transports.ITransportListener;
import org.opennaas.extensions.roadm.wonesys.transports.rawsocket.RawSocketTransport;
import org.osgi.service.event.Event;

/**
 * 
 * @author isart
 * 
 */
public class CommandResponseListener implements ITransportListener
{
	private final static Log		log		=
													LogFactory.getLog(CommandResponseListener.class);

	private final CountDownLatch	latch	= new CountDownLatch(1);
	private final String			commandId;
	private String					response;

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
