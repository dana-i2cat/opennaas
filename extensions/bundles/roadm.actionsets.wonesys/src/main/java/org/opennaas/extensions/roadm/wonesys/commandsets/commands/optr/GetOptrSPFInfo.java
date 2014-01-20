package org.opennaas.extensions.roadm.wonesys.commandsets.commands.optr;

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

public class GetOptrSPFInfo extends WonesysCommand {

	private static final String	DATA_LENGTH	= "0100";

	private String				chassis;
	private String				slot;

	private String				cardType;

	private String				sfpNumber;

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub

		// Pot. Salida (dBm, Float)
		// Pot. Entrada (dBm, Float)
		// Signal Detected*** (Select8) ***Laser Status: 0x00: OFF, 0x01: ON, 0x02: AUTO

	}

	@Override
	protected String getWonesysCommandDeviceId() {
		return chassis + slot;
	}

	@Override
	protected String getWonesysCommandId() {

		if (cardType.equals("08"))
			return "0804";

		return cardType + "03";
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
