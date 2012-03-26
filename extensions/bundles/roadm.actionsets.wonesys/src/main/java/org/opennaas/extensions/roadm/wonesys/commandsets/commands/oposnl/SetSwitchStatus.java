package org.opennaas.extensions.roadm.wonesys.commandsets.commands.oposnl;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.core.resources.command.CommandException;

public class SetSwitchStatus extends WonesysCommand {

	private static final String	COMMAND_ID	= "0f03";
	private static final String	DATA_LENGTH	= "0200";

	private String				chassis;
	private String				slot;

	// 0 or 1
	private String				switchNumber;
	// *:Port is 0 for auto switching, 1 for forcing port 1 and 2 for forcing port 2.
	private String				port;

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub

		// OK
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
		return switchNumber + port;
	}

}
