package org.opennaas.extensions.roadm.wonesys.commandsets.commands.optr.optr10g.tn;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.core.resources.command.CommandException;

public class GetChannelPlan extends WonesysCommand {

	private static final String	COMMAND_ID	= "0508";
	private static final String	DATA_LENGTH	= "0000";

	private String				chassis;
	private String				slot;

	private String				cardType	= "05";

	public GetChannelPlan(String chassis, String slot) {
		this.chassis = chassis;
		this.slot = slot;
	}

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
		return "";
	}

}
