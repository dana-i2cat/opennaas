package org.opennaas.extensions.roadm.wonesys.commandsets.commands.optr.optr2g5exc_pm;

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

public class GetSFPInfoSDH extends WonesysCommand {

	private static final String	COMMAND_ID	= "0806";
	private static final String	DATA_LENGTH	= "0100";

	private String				chassis;
	private String				slot;

	private String				sfpNumber;

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub
		// 0x06 0x00
		// LOF Alarm (Select8)***
		// B1 Error (Select 8)***
		// MS-AIS (Select8)***
		// B2 Error (Select8)***
		// RS-TIM Alarm (Select8)***
		// MS-RDI Alarm (Select8)***
		// *** Alarm/Error 0x00: No alarms/ errors in the previous second, 0x01: Alarms/Errors in the previous second

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
		return sfpNumber;
	}

}
