package org.opennaas.extensions.roadm.wonesys.commandsets.commands.optr.optr10g._1p1;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.core.resources.command.CommandException;

public class SetPowerThreshold extends WonesysCommand {

	private static final String	COMMAND_ID	= "0505";
	private static final String	DATA_LENGTH	= "0800";

	private String				chassis;
	private String				slot;

	private String				cardType	= "05";

	private String				portBThreshold_dBm;
	private String				portCThreshold_dBm;

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
		return COMMAND_ID;
	}

	@Override
	protected String getWonesysCommandRequiredDataLength() {
		return DATA_LENGTH;
	}

	@Override
	protected String getWonesysCommandData() {
		return portBThreshold_dBm + portCThreshold_dBm;
	}

}
