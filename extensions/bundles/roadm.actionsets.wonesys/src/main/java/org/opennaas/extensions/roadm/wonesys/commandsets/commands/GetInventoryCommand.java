package org.opennaas.extensions.roadm.wonesys.commandsets.commands;

import java.io.IOException;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysResponse;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.extensions.router.model.utils.OpticalSwitchCardFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GetInventoryCommand extends WonesysCommand {

	Log							log			= LogFactory.getLog(WonesysCommand.class);

	private static final String	COMMAND_ID	= "ff01";
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

		OpticalSwitchCardFactory factory;
		try {
			factory = new OpticalSwitchCardFactory();
		} catch (IOException e) {
			throw new CommandException("Failed to load received data into model. Error loading card profiles file:", e);
		}

		String responseData = commandResponse.getInformation();

		// ID (2B) tipo (1B) subtipo (1B) = 4B per result, 2chars x Byte
		int resultLength = 4 * 2;
		int resultsCount = responseData.length() / resultLength;
		String[] results = new String[resultsCount];

		for (int i = 0; i < resultsCount; i++) {
			results[i] = responseData.substring(i * resultLength, (i + 1) * resultLength);

			String schasis = results[i].substring(0, 2);
			String sslot = results[i].substring(2, 4);
			String stype = results[i].substring(4, 6);
			String ssubtype = results[i].substring(6, 8);

			int chasis = Integer.parseInt(schasis, 16);
			int slot = Integer.parseInt(sslot, 16);
			int type = Integer.parseInt(stype, 16);
			int subtype = Integer.parseInt(ssubtype, 16);

			log.info("Chasis:" + chasis + " Slot:" + slot + " Type:" + type + " SubType:" + subtype);

			ProteusOpticalSwitchCard card = factory.createCard(chasis, slot, type, subtype);
			((ProteusOpticalSwitch) model).addLogicalDevice(card);
		}

		factory.addHardcodedCardConnections((ProteusOpticalSwitch) model);

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
