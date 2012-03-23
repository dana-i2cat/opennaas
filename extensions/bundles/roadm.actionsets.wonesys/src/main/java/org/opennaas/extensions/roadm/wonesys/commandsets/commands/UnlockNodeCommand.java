package org.opennaas.extensions.roadm.wonesys.commandsets.commands;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysResponse;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;

public class UnlockNodeCommand extends WonesysCommand {

	private static final String	COMMAND_ID	= "ff04";
	private static final String	DATA_LENGTH	= "0000";

	@Override
	public void parseResponse(Object response, Object model) throws CommandException {
		WonesysResponse commandResponse = (WonesysResponse) response;

		if (commandResponse.getStatus().equals(Response.Status.ERROR)) {
			if (commandResponse.getErrors().size() > 0)
				throw new CommandException(commandResponse.getErrors().get(0));
			else
				throw new CommandException("Command Failed");
		}

		String responseData = commandResponse.getInformation();

		if (!responseData.equals(WonesysCommand.SET_OK_STATUS)) {
			throw new CommandException();
		}
	}

	@Override
	protected String getWonesysCommandDeviceId() {
		return MONITOR_DEVICE_ID;
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
