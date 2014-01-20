package org.opennaas.extensions.roadm.wonesys.commandsets.commands;

/*
 * #%L
 * OpenNaaS :: ROADM :: W-Onesys Actionset
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

import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysResponse;

public class LockNodeCommand extends WonesysCommand {

	private static final String	COMMAND_ID	= "ff03";
	private static final String	DATA_LENGTH	= "0000";

	@Override
	public void parseResponse(Object response, Object model) throws CommandException {
		WonesysResponse commandResponse = (WonesysResponse) response;

		if (commandResponse.getStatus().equals(Response.Status.ERROR)) {
			if (commandResponse.getErrors().size() > 0)
				throw new CommandException(commandResponse.getErrors().get(0));
			else
				throw new CommandException("Command Failed");
		}

		String responseData = commandResponse.getInformation();

		if (!responseData.equals(WonesysCommand.SET_OK_STATUS)) {
			throw new CommandException();
		}
	}

	@Override
	protected String getWonesysCommandDeviceId() {
		return MONITOR_DEVICE_ID;
	}

	@Override
	protected String getWonesysCommandId() {
		return COMMAND_ID;
	}

	@Override
	protected String getWonesysCommandRequiredDataLength() {
		return DATA_LENGTH;
	}

	@Override
	protected String getWonesysCommandData() {
		return "";
	}

}
