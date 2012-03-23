package org.opennaas.extensions.roadm.wonesys.commandsets.commands.opm;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.core.resources.command.CommandException;

public class GetPowerOSNR extends WonesysCommand {

	private static final String	COMMAND_ID	= "1101";
	private static final String	DATA_LENGTH	= "0200";

	private String				chassis;
	private String				slot;

	// 0 or 1
	private String				channel;

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub

		// 0x08 0x00
		// Potencia dBm (4B)
		// OSNR dB (4B)

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
		return channel;
	}

}
