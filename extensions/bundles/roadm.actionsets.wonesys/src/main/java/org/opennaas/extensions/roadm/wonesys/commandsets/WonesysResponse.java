package org.opennaas.extensions.roadm.wonesys.commandsets;

import java.util.List;
import java.util.Vector;

import org.opennaas.core.resources.command.Response;

public class WonesysResponse extends Response {

	private WonesysMessage	wonesysResponseMessage	= null;

	public WonesysResponse(Object cmdQuery, Object rawResponse) {

		// set sent msg
		String query = (String) cmdQuery;
		setSentMessage(query);

		// set wonesysResponseMessage
		String response = (String) rawResponse;
		try {
			wonesysResponseMessage = new WonesysMessage(response);
		} catch (Exception e) {
			// wonesysMessage creation failed
			wonesysResponseMessage = null;
			getErrors().add("Invalid response message. " + e.getMessage());
		}

		if (wonesysResponseMessage != null) {
			// set errors
			fillErrors(wonesysResponseMessage);

			// set info
			String data = wonesysResponseMessage.getData();
			setInformation(data);
		}

		// set status
		// Status.WAIT is not to be used here,
		// as wonesys respond only when a command has finished.
		if (getErrors().size() > 0)
			setStatus(Status.ERROR);
		else
			setStatus(Status.OK);
	}

	public WonesysMessage getWonesysResponseMessage() {
		return wonesysResponseMessage;
	}

	/**
	 * Fills this Response errors list with errors contained in wonesysMessage (if any). If no errors are present, errors is filled with an empty
	 * list.
	 *
	 * @param wonesysMessage
	 */
	private void fillErrors(WonesysMessage wonesysMessage) {
		String commandId = wonesysMessage.getCommandId();

		List<String> errorsList = new Vector<String>();
		if (WonesysErrorsInventory.isError(commandId)) {
			errorsList.add(WonesysErrorsInventory.getErrorDescription(commandId));
		}
		setErrors(errorsList);
	}
}
