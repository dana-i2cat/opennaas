package org.opennaas.extensions.roadm.wonesys.actionsets.actions;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysResponse;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;

public abstract class WonesysAction extends Action {

	public Response checkResponse(Object message, Object rawResponse) {
		return new WonesysResponse(message, rawResponse);
	}

	public void updateStatusFromResponses(ActionResponse actionResponse) {
		for (Response response : actionResponse.getResponses()) {
			if (response.getStatus() == Response.Status.ERROR) {
				actionResponse.setStatus(ActionResponse.STATUS.ERROR);
				return;
			}
		}
	}

	/**
	 * changes the order of bytes in the given hex string. For given 0,1,2,...,n returns n,...,2,1,0
	 */
	public static String convertLittleBigEndian(String value) {

		StringBuilder builder = new StringBuilder();

		int totalBytes = value.length() / 2;
		for (int i = totalBytes; i > 0; i--) {
			builder.append(value.substring(i * 2 - 2, i * 2));
		}
		return builder.toString();
	}

	/**
	 *
	 *
	 * @param data
	 * @param numberOfBytes
	 * @return
	 */
	public static String toByteHexString(int data, int numberOfBytes) {

		String hexStr = Integer.toHexString(data);

		if (hexStr.length() > numberOfBytes * 2)
			throw new IllegalArgumentException(
					"Given data requires more than specified numberOfBytes. Data:" + data + " NumberOfBytes: " + numberOfBytes);

		// follows byteHexString format
		if (hexStr.length() % 2 != 0)
			hexStr = "0" + hexStr;

		// use specified number of bytes
		while (hexStr.length() < numberOfBytes * 2) {
			hexStr = "00" + hexStr;
		}
		hexStr = WonesysCommand.convertLittleBigEndian(hexStr);
		return hexStr;
	}
}
