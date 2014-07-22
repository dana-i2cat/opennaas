package org.opennaas.extensions.roadm.wonesys.commandsets.commands.oposnl;

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
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;

public class GetSwitchStatus extends WonesysCommand {

	private static final String	COMMAND_ID	= "0f01";
	private static final String	DATA_LENGTH	= "0100";

	private String				chassis;
	private String				slot;

	// 0 or 1
	private String				switchNumber;

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub

		// 0x01 0x00 Port (Select8)*
		// *:Port is 0 for auto switching, 1 for forcing port 1 and 2 for forcing port 2.

	}

	@Override
	protected String getWonesysCommandDeviceId() {
		return chassis + slot;
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
		return switchNumber;
	}

}
