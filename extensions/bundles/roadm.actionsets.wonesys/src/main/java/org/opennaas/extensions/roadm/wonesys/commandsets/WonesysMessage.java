package org.opennaas.extensions.roadm.wonesys.commandsets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Wonesys messages are byte chunks with the following format:
 *
 * Header (2B) | DeviceID (2B) | Reserved (2B) | CommandID (2B) | Reserved (4B) | DataLength (2B) | Data | XOR (1B) | EOS (1B)
 *
 * @author isart
 *
 */
public final class WonesysMessage {

	/** The logger **/
	Log							log					= LogFactory.getLog(WonesysMessage.class);

	private static final String	HEADER				= "5910";
	private static final String	RESERVED			= "ffff";
	private static final String	EOS					= "00";

	private static final int	deviceIDIndex		= 2;
	private static final int	commandIDIndex		= 6;
	private static final int	dataLengthIndex		= 12;
	private static final int	dataIndex			= 14;

	/**
	 * Length of the shortest valid command: 14 bytes + 0 bytes (data) + XOR + EOS
	 */
	private static final int	MIN_COMMAND_LENGTH	= (dataIndex + 0 + 1 + 1) * 2;

	private final String		msg;

	public WonesysMessage(String completeMsg) throws Exception {
		checkCorrectiviness(completeMsg);
		this.msg = completeMsg;
	}

	public WonesysMessage(String deviceId, String commandId, String data) {
		this(deviceId, commandId, getDataLength(data), data);
	}

	public WonesysMessage(String deviceId, String commandId, String dataLength, String data, boolean checkDataLength) throws Exception {
		this(deviceId, commandId, dataLength, data);
		if (checkDataLength) {
			if (!dataLength.equals(getDataLength(data)))
				throw new Exception("Given data does not match required data length");
		}
	}

	private WonesysMessage(String deviceId, String commandId, String dataLength, String data) {

		StringBuilder builder = new StringBuilder();
		builder.append(HEADER);
		builder.append(deviceId);
		builder.append(RESERVED);
		builder.append(commandId);
		builder.append(RESERVED);
		builder.append(RESERVED);
		builder.append(dataLength);
		builder.append(data);

		String cmbBase = builder.toString();
		String xor = getXOR(cmbBase);

		builder.append(xor);
		builder.append(EOS);

		this.msg = builder.toString();
		log.info("Created WonesysMessage " + this.msg);
	}

	public String getDeviceId() {
		return this.msg.substring(deviceIDIndex * 2, deviceIDIndex * 2 + 2 * 2);
	}

	public String getCommandId() {
		return this.msg.substring(commandIDIndex * 2, commandIDIndex * 2 + 2 * 2);
	}

	public String getData() {
		String dataLengthStr = this.msg.substring(dataLengthIndex * 2, dataLengthIndex * 2 + 2 * 2);

		// transform str into real byte length
		// Invert byte order (Big endian stuff)
		String datasizeStr = dataLengthStr.substring(2, 4) + dataLengthStr.substring(0, 2);
		int dataLength = Integer.parseInt(datasizeStr, 16);

		return this.msg.substring(dataIndex * 2, dataIndex * 2 + dataLength * 2);
	}

	public String toString() {
		return this.msg;
	}

	// private static String getDataLength(String data) {
	// // XXX: This works only for data smaller than 16 bytes
	// String dataLength = "00";
	// if (data.length() > 0) {
	// dataLength = Integer.toHexString((data.length() / 2) + 1);
	// if (dataLength.length() < 2) {
	// dataLength = "0" + dataLength;
	// }
	// }
	// dataLength += "00";
	//
	// return dataLength;
	// }

	/**
	 *
	 * @param data
	 * @return data length as an HexString in BigEndian. Returned String must be 4 chars long
	 */
	private static String getDataLength(String data) {
		String dataLength = Integer.toHexString(data.length() / 2);

		// add trailing 0's
		if (dataLength.length() % 2 != 0) // odd dataLength.length()
			dataLength = "0" + dataLength;
		if (dataLength.length() < 4)
			dataLength = "00" + dataLength;

		// to big endian
		dataLength = dataLength.substring(2, 4) + dataLength.substring(0, 2);
		return dataLength;
	}

	private String getXOR(String cmd) {

		int xor = Integer.parseInt(cmd.substring(0, 2), 16)
				^ Integer.parseInt(cmd.substring(2, 4), 16);
		for (int i = 4; i <= cmd.length() - 2; i++) {
			xor = xor ^ Integer.parseInt(cmd.substring(i, i + 2), 16);
			i++;
		}
		String hxor = Integer.toHexString(xor);
		if (hxor.length() < 2) {
			hxor = "0" + hxor;
		}
		return hxor;
	}

	private void checkCorrectiviness(String command) throws Exception {
		// TODO: Enhance checking, throw specific exceptions
		if (command.length() < WonesysMessage.MIN_COMMAND_LENGTH)
			throw new Exception("Malformed message [" + command + "]");
	}

}
