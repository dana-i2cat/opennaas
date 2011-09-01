package net.i2cat.luminis.commandsets.wonesys.commands.optr;

import net.i2cat.luminis.commandsets.wonesys.WonesysCommand;
import net.i2cat.nexus.resources.command.CommandException;

public class SetOptrLaser extends WonesysCommand {

	private static final String	DATA_LENGTH	= "0200";

	private String				chassis;
	private String				slot;

	private String				cardType;

	private String				spfNumber;

	// Laser Status: 0x00: OFF, 0x01: ON, 0x02: AUTO
	private String				status;

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
		if (cardType.equals("08"))
			return "0808";

		return cardType + "04";
	}

	@Override
	protected String getWonesysCommandRequiredDataLength() {
		return DATA_LENGTH;
	}

	@Override
	protected String getWonesysCommandData() {
		return spfNumber + status;
	}

}
