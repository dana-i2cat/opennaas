package org.opennaas.extensions.roadm.wonesys.commandsets.commands.tdcm;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.core.resources.command.CommandException;

public class SetTDCMChannel extends WonesysCommand {

	private static final String	COMMAND_ID	= "1002";
	private static final String	DATA_LENGTH	= "0200";

	private String				chassis;
	private String				slot;

	private String				channel;

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub

		// Num channel

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
