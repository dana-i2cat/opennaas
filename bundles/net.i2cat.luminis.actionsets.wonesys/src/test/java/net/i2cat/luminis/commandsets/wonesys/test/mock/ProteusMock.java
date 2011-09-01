package net.i2cat.luminis.commandsets.wonesys.test.mock;

import java.util.HashMap;

import net.i2cat.luminis.commandsets.wonesys.WonesysCommand;

public class ProteusMock {

	String						channelPlan		= "05002000880100";				// datalength (0500) + data
	String[]					initialChannels	= new String[] {
												"00", "00", "01", "00", "00", "00", "00", "00", "00", "00",
												"00", "00", "00", "00", "00", "00", "00", "00", "00", "80",
												"00", "00", "00", "00", "00", "00", "00", "00", "00", "00",
												"00", "00", "00", "00", "00", "00", "00", "00", "00", "00",
												"00", "00", "00", "00", "00" };

	HashMap<String, String[]>	cardsChannels	= new HashMap<String, String[]>();

	public Object execCommand(String message) {

		String response = null;
		if (message.equals("5910ffffffffff01ffffffff0000b700")) {
			// GET INVENTORY
			response = "5910ffffffffff01ffffffff200000010b0100030b0100070e00000b0e0000100b2000110b2001000400010105009800";
		} else if (message.substring(12, 16).equals("0b03")) {
			// GET CHANNEL PLAN
			response = message.substring(0, 24);
			response += channelPlan;
			response += getXOR(response) + "00";

		} else if (message.substring(12, 16).equals("0b04")) {
			// GET CHANNELS
			String cardID = message.substring(4, 8);
			String[] channels = cardsChannels.get(cardID);

			if (channels == null) {
				channels = new String[initialChannels.length];
				for (int i = 0; i < initialChannels.length; i++) {
					channels[i] = initialChannels[i];
				}
				cardsChannels.put(cardID, channels);
			}

			StringBuilder b = new StringBuilder();
			for (String c : channels) {
				b.append(c);
			}
			response = message.substring(0, 24);
			response += "2d00" + b.toString();
			response += getXOR(response) + "00";

		} else if (message.substring(12, 16).equals("0b01")) {
			// GET CHANNEL INFO

			String cardID = message.substring(4, 8);

			String channelNumStr = message.substring(28, 32);

			int channelNum = Integer.parseInt(WonesysCommand.convertLittleBigEndian(channelNumStr), 16);
			response = message.substring(0, 24);
			response += "0100" + cardsChannels.get(cardID)[channelNum];
			response += getXOR(response) + "00";

		} else if (message.substring(12, 16).equals("0b02")) {
			// SET CHANNEL
			String cardID = message.substring(4, 8);
			String[] channels = cardsChannels.get(cardID);

			if (channels == null) {
				channels = new String[initialChannels.length];
				for (int i = 0; i < initialChannels.length; i++) {
					channels[i] = initialChannels[i];
				}
				cardsChannels.put(cardID, channels);
			}

			// Set channel
			String channelNumStr = message.substring(28, 32);
			channelNumStr = channelNumStr.substring(2, 4) + channelNumStr.subSequence(0, 2);
			int channelNum = Integer.parseInt(channelNumStr, 16);
			String port = message.substring(32, 34);
			channels[channelNum] = port;

			response = message.substring(0, 24);
			response += "010000"; // datalength (0100) + data (00)
			response += getXOR(response) + "00";

		}

		return response;
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
}
