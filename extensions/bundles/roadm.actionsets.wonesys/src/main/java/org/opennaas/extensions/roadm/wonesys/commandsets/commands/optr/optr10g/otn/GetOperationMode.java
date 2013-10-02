package org.opennaas.extensions.roadm.wonesys.commandsets.commands.optr.optr10g.otn;

import org.opennaas.core.resources.command.CommandException;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;

public class GetOperationMode extends WonesysCommand {

	private static final String	COMMAND_ID	= "0506";
	private static final String	DATA_LENGTH	= "0000";

	private String				chassis;
	private String				slot;

	private String				cardType	= "05";

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
