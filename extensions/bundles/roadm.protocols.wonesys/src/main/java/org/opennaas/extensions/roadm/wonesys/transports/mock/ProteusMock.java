package org.opennaas.extensions.roadm.wonesys.transports.mock;

import java.util.HashMap;

public class ProteusMock {

	// datalength (0500) data, info from chassis 0 , slot 1
	String						channelPlan		= "05002000880100";
	int							firstChannel	= Integer.parseInt(convertLittleBigEndian(channelPlan.substring(4, 8)), 16);
	int							lastChannel		= Integer.parseInt(convertLittleBigEndian(channelPlan.substring(8, 12)), 16);
	int							channelGap		= (int) Math.pow(2, 3 - Integer.parseInt(convertLittleBigEndian(channelPlan.substring(12, 14)), 16));
	boolean						locked			= false;

	String[]					initialChannels	= new String[] {
												"00", "00", "01", "00", "00", "00", "00", "00", "00", "00",
												"00", "00", "00", "00", "00", "00", "00", "00", "00", "80",
												"00", "00", "00", "00", "00", "00", "00", "00", "00", "00",
												"00", "00", "00", "00", "00", "00", "00", "00", "00", "00",
												"00", "00", "00", "00", "00" };

	HashMap<String, String[]>	cardsChannels	= new HashMap<String, String[]>();
	MockTransport				mockTransport	= null;

	public ProteusMock(MockTransport mockTransport) {
		this.mockTransport = mockTransport;
	}

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

		} else if (message.equals("5910ffffffffff03ffffffff0000b500")) {
			// LOCK NODE
			// no session control in mocks
			locked = true;
			response = "5910ffffffffff03ffffffff010000b400";
		} else if (message.equals("5910ffffffffff04ffffffff0000b200")) {
			// UNLOCK NODE
			if (!locked)
				response = "5910ffffffff0c34ffffffff00007100";
			else {
				locked = false;
				response = "5910ffffffffff04ffffffff010000b300";
			}
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

			int channelNum = Integer.parseInt(convertLittleBigEndian(channelNumStr), 16);
			int channelIndex = (channelNum - firstChannel) / channelGap;

			response = message.substring(0, 24);
			response += "0100" + cardsChannels.get(cardID)[channelIndex];
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

			int channelIndex = (channelNum - firstChannel) / channelGap;
			channels[channelIndex] = port;

			response = message.substring(0, 24);
			response += "010000"; // datalength (0100) + data (00)
			response += getXOR(response) + "00";

			if (mockTransport != null){
				String alarmMessage = "FFFF0000" + cardID + "01FF80";
				mockTransport.notifyListeners(alarmMessage);
			}
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

	/**
	 * changes the order of bytes in the given hex string. For given 0,1,2,...,n returns n,...,2,1,0
	 */
	private static String convertLittleBigEndian(String value) {

		StringBuilder builder = new StringBuilder();

		int totalBytes = value.length() / 2;
		for (int i = totalBytes; i > 0; i--) {
			builder.append(value.substring(i * 2 - 2, i * 2));
		}
		return builder.toString();
	}
}
