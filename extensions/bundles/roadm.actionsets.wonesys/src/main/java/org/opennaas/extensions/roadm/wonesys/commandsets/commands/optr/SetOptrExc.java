package org.opennaas.extensions.roadm.wonesys.commandsets.commands.optr;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.core.resources.command.CommandException;

public class SetOptrExc extends WonesysCommand {

	private String	chassis;
	private String	slot;

	private String	cardType;

	private String	mode;
	private String	modeData;

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
		return cardType + "02";
	}

	/**
	 * No required dataLenght. It may change.
	 */
	@Override
	protected String getWonesysCommandRequiredDataLength() {
		return "";
	}

	@Override
	protected String getWonesysCommandData() {
		return mode + modeData;
	}

	@Override
	public void initialize() throws CommandException {
		try {
			if (this.cmd == null)
				// do not force dataLength as it is variable
				this.cmd = craftCommand(getWonesysCommandDeviceId(), getWonesysCommandId(), getWonesysCommandData());
		} catch (Exception e) {
			throw new CommandException();
		}
	}

}
