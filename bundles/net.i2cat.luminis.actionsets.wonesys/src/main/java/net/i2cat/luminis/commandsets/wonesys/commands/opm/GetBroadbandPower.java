package net.i2cat.luminis.commandsets.wonesys.commands.opm;

import net.i2cat.luminis.commandsets.wonesys.WonesysCommand;
import net.i2cat.nexus.resources.command.CommandException;

public class GetBroadbandPower extends WonesysCommand {

	private static final String	COMMAND_ID	= "1102";
	private static final String	DATA_LENGTH	= "0000";

	private String				chassis;
	private String				slot;

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub

		// 0x04 0x00
		// Potencia dBm (4B)

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
		return "";
	}

}
