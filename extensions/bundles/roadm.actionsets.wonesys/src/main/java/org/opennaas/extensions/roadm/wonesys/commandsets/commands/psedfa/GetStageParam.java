package org.opennaas.extensions.roadm.wonesys.commandsets.commands.psedfa;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.core.resources.command.CommandException;

public class GetStageParam extends WonesysCommand {

	private static final String	DATA_LENGTH	= "0100";

	private String				chassis;
	private String				slot;

	private String				stage;

	/**
	 * 01 -> PowerIn<br>
	 * 02 -> PowerOut<br>
	 * 03 -> RealGain
	 */
	private String				paramId;

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
		return "0e" + paramId;
	}

	@Override
	protected String getWonesysCommandRequiredDataLength() {
		return DATA_LENGTH;
	}

	@Override
	protected String getWonesysCommandData() {
		return stage;
	}

}
