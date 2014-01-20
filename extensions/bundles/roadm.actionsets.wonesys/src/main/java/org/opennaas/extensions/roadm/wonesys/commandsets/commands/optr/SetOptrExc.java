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

public class SetOptrExc extends WonesysCommand {

	private String	chassis;
	private String	slot;

	private String	cardType;

	private String	mode;
	private String	modeData;

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getWonesysCommandDeviceId() {
		return chassis + slot;
	}

	@Override
	protected String getWonesysCommandId() {
		return cardType + "02";
	}

	/**
	 * No required dataLenght. It may change.
	 */
	@Override
	protected String getWonesysCommandRequiredDataLength() {
		return "";
	}

	@Override
	protected String getWonesysCommandData() {
		return mode + modeData;
	}

	@Override
	public void initialize() throws CommandException {
		try {
			if (this.cmd == null)
				// do not force dataLength as it is variable
				this.cmd = craftCommand(getWonesysCommandDeviceId(), getWonesysCommandId(), getWonesysCommandData());
		} catch (Exception e) {
			throw new CommandException();
		}
	}

}
