package org.opennaas.extensions.roadm.wonesys.commandsets.commands.tdcm;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.core.resources.command.CommandException;

public class GetTDCMValues extends WonesysCommand {

	private static final String	COMMAND_ID	= "1001";
	private static final String	DATA_LENGTH	= "0000";

	private String				chassis;
	private String				slot;

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub

		// 0x06 0x00
		// Num canal (2B)
		// Dispersion (4B)

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
