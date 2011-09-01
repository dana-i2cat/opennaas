package net.i2cat.luminis.commandsets.wonesys.commands.optr.optr10g.tn;

import net.i2cat.luminis.commandsets.wonesys.WonesysCommand;
import net.i2cat.nexus.resources.command.CommandException;

public class SetDWDMChannel extends WonesysCommand {

	private static final String	COMMAND_ID	= "0509";
	private static final String	DATA_LENGTH	= "0200";

	private String				chassis;
	private String				slot;

	private String				cardType	= "05";

	private String				channel;

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
		return channel;
	}

}
