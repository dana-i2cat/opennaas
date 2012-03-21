package net.i2cat.luminis.commandsets.wonesys.commands.optr.optr2g5exc_pm;

import net.i2cat.luminis.commandsets.wonesys.WonesysCommand;
import org.opennaas.core.resources.command.CommandException;

public class GetSFPInfoOTN extends WonesysCommand {

	private static final String	COMMAND_ID	= "0807";
	private static final String	DATA_LENGTH	= "0100";

	private String				chassis;
	private String				slot;

	private String				sfpNumber;

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub

		// TBD

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
