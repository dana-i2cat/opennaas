package org.opennaas.extensions.roadm.wonesys.commandsets.commands.optr.optr2g5exc_pm;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.core.resources.command.CommandException;

public class GetCardMode extends WonesysCommand {

	private static final String	COMMAND_ID	= "0803";
	private static final String	DATA_LENGTH	= "0000";

	private String				chassis;
	private String				slot;

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub

		// 0x02 0x00 Protocol (select8)***** Rate (select8)*****
		// *****Card mode:
		// Protocol: 0x01: SDH, 0x02: Ethernet, 0x03: OTN
		// Rate: 0x01: STM1, 0x02: STM4, 0x03: GbE, 0x04: STM16, 0x05: OTU1

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
