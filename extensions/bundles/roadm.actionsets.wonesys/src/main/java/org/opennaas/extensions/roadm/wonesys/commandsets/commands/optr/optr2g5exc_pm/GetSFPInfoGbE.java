package org.opennaas.extensions.roadm.wonesys.commandsets.commands.optr.optr2g5exc_pm;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.core.resources.command.CommandException;

public class GetSFPInfoGbE extends WonesysCommand {

	private static final String	COMMAND_ID	= "0805";
	private static final String	DATA_LENGTH	= "0100";

	private String				chassis;
	private String				slot;

	private String				sfpNumber;

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub
		// 0x02 0x00
		// Link Alarm (Select8)***
		// FCS Error (Select8)***
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
