package org.opennaas.extensions.roadm.wonesys.commandsets.commands.oposnl;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.core.resources.command.CommandException;

public class GetSwitchParams extends WonesysCommand {

	private static final String	COMMAND_ID	= "0f02";
	private static final String	DATA_LENGTH	= "0100";

	private String				chassis;
	private String				slot;

	// 0 or 1
	private String				switchNumber;

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub

		// 0x0C 0x00
		// Pot. Max (Float)**
		// Pot. Min (Float)**
		// Delta (Float)***
		// **: Pot Min and Pot max in dBm
		// ***: Delta in dB

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
