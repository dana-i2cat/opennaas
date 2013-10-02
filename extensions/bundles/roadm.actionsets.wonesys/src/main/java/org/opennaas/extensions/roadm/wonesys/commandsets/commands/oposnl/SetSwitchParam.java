package org.opennaas.extensions.roadm.wonesys.commandsets.commands.oposnl;

import org.opennaas.core.resources.command.CommandException;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;

public class SetSwitchParam extends WonesysCommand {

	private static final String	DATA_LENGTH	= "0200";

	private String				chassis;
	private String				slot;

	// 0 or 1
	private String				switchNumber;
	private String				paramValue;

	/**
	 * "04" -> Max Power<br>
	 * "05" -> Min Power<br>
	 * "06" -> Delta
	 */
	private String				paramId;

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
		return "0f" + paramId;
	}

	@Override
	protected String getWonesysCommandRequiredDataLength() {
		return DATA_LENGTH;
	}

	@Override
	protected String getWonesysCommandData() {
		return switchNumber + paramValue;
	}

}
