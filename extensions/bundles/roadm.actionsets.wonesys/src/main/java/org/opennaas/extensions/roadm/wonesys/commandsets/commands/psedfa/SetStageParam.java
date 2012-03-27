package org.opennaas.extensions.roadm.wonesys.commandsets.commands.psedfa;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.core.resources.command.CommandException;

public class SetStageParam extends WonesysCommand {

	private static final String	DATA_LENGTH	= "0500";

	private String				chassis;
	private String				slot;

	private String				stage;

	/**
	 * 01 -> PowerIn<br>
	 * 02 -> PowerOut<br>
	 */
	private String				paramId;
	private String				paramValue;

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
		String commandIdR = "0" + Integer.toString((Integer.parseInt(paramId) + 4));
		return "0e" + commandIdR;
	}

	@Override
	protected String getWonesysCommandRequiredDataLength() {
		return DATA_LENGTH;
	}

	@Override
	protected String getWonesysCommandData() {
		return stage + paramValue;
	}

}
