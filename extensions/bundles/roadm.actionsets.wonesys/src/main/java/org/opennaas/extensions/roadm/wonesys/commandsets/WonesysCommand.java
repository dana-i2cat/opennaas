package org.opennaas.extensions.roadm.wonesys.commandsets;

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

import org.opennaas.core.resources.command.Command;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;

public abstract class WonesysCommand extends Command {

	public static final String	MONITOR_DEVICE_ID	= "ffff";
	public static final String	SET_OK_STATUS		= "00";

	protected String			cmd					= null;

	protected String craftCommand(String deviceID, String commandID, String data) {
		WonesysMessage wMessage = new WonesysMessage(deviceID, commandID, data);
		return wMessage.toString();
	}

	protected String craftCommand(String deviceID, String commandID, String requiredDataLength, String data) throws Exception {
		WonesysMessage wMessage = new WonesysMessage(deviceID, commandID, requiredDataLength, data, true);
		return wMessage.toString();
	}

	public Response checkResponse(Object rawResponse) {
		return new WonesysResponse(message(), rawResponse);
	}

	@Override
	public void initialize() throws CommandException {
		try {
			if (this.cmd == null)
				this.cmd = craftCommand(getWonesysCommandDeviceId(), getWonesysCommandId(), getWonesysCommandRequiredDataLength(),
						getWonesysCommandData());
		} catch (Exception e) {
			throw new CommandException();
		}
	}

	public Object message() {
		return this.cmd;
	}

	public abstract void parseResponse(Object response, Object model) throws CommandException;

	protected abstract String getWonesysCommandDeviceId();

	protected abstract String getWonesysCommandId();

	protected abstract String getWonesysCommandRequiredDataLength();

	protected abstract String getWonesysCommandData();

	/**
	 * changes the order of bytes in the given hex string. For given 0,1,2,...,n returns n,...,2,1,0
	 */
	public static String convertLittleBigEndian(String value) {

		StringBuilder builder = new StringBuilder();

		int totalBytes = value.length() / 2;
		for (int i = totalBytes; i > 0; i--) {
			builder.append(value.substring(i * 2 - 2, i * 2));
		}
		return builder.toString();
	}

	/**
	 * 
	 * 
	 * @param data
	 * @param numberOfBytes
	 * @return
	 */
	public static String toByteHexString(int data, int numberOfBytes) {

		String hexStr = Integer.toHexString(data);

		if (hexStr.length() > numberOfBytes * 2)
			throw new IllegalArgumentException(
					"Given data requires more than specified numberOfBytes. Data:" + data + " NumberOfBytes: " + numberOfBytes);

		// follows byteHexString format
		if (hexStr.length() % 2 != 0)
			hexStr = "0" + hexStr;

		// use specified number of bytes
		while (hexStr.length() < numberOfBytes * 2) {
			hexStr = "00" + hexStr;
		}
		hexStr = WonesysCommand.convertLittleBigEndian(hexStr);
		return hexStr;
	}

}
