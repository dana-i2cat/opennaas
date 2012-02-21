package net.i2cat.mantychore.commandsets.junos.commons;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;

/**
 * It is used to parse different messages
 * 
 * @author Carlos Baez Ruiz
 * 
 */
public class IPUtilsHelper {

	public static long ipv4StringToLong(String ip) throws IOException {
		// transform String ([0..255].[0..255].[0..255].[0..255]) into long
		InetAddress address = Inet4Address.getByName(ip);
		ByteBuffer bb = ByteBuffer.wrap(address.getAddress());
		long ipLong;
		if (address.getAddress().length > 4) {
			ipLong = bb.getLong();// reads 8 bytes and creates a long
		} else {
			ipLong = bb.getInt(); // reads 4 bytes and creates an int
		}
		return ipLong;
	}

	public static String ipv4LongToString(long ip) throws IOException {
		// transform long into String ([0..255].[0..255].[0..255].[0..255])
		ByteBuffer bb = ByteBuffer.allocate(8).putLong(ip);
		byte[] bytes = new byte[4];
		bb.position(4);
		bb.get(bytes, 0, 4); // read 4 bytes starting at position 4.

		InetAddress address = Inet4Address.getByAddress(bytes);
		return address.getHostAddress();
	}

	public static short[] parseStrIPToBytesIP(String IP) {
		short[] newIP = new short[4];
		String[] splittedIP = IP.split("\\.");

		newIP[0] = Short.parseShort(splittedIP[0]);
		newIP[1] = Short.parseShort(splittedIP[1]);
		newIP[2] = Short.parseShort(splittedIP[2]);
		newIP[3] = Short.parseShort(splittedIP[3]);

		return newIP;

	}

	public static int parseAddressToInt(String address) {

		String[] ipAddress = address.split("\\.");

		int bitPrefixA = Integer.parseInt(ipAddress[0]) << 24;
		int bitPrefixB = bitPrefixA | (Integer.parseInt(ipAddress[1]) << 16);
		int bitPrefixC = bitPrefixB | (Integer.parseInt(ipAddress[2]) << 8);
		int result = bitPrefixC | (Integer.parseInt(ipAddress[3]));

		return result;
	}

	/* extra methods */
	public static String parseShortToLongIpv4NetMask(String netmask) {
		String netmaskResult = "";
		int intNetmask = 0;
		try {
			intNetmask = Integer.parseInt(netmask);
		} catch (Exception e) {
			return netmask;
		}

		if (intNetmask <= 0)
			return "0.0.0.0";
		if (intNetmask >= 32)
			return "255.255.255.255";

		// create the mask in bits
		int andMask = 0x000000ff;
		int bitNetmask = 0xffffffff << (32 - intNetmask);
		int bitprefixA = ((bitNetmask >> 24) & andMask);
		int bitprefixB = ((bitNetmask >> 16) & andMask);
		int bitprefixC = ((bitNetmask >> 8) & andMask);
		int bitprefixD = ((bitNetmask >> 0) & andMask);

		/* parse to string */
		netmaskResult = String.valueOf(bitprefixA) + "." + String.valueOf(bitprefixB) + "." + String.valueOf(bitprefixC) + "." + String
				.valueOf(bitprefixD);

		return netmaskResult;
	}

	public static String parseLongToShortIpv4NetMask(String netmask) {
		int MAX_MASK = 32;

		String[] arrayLongNetmask = netmask.split("\\.");

		int bitPrefixA = Integer.parseInt(arrayLongNetmask[0]) << 24;
		int bitPrefixB = bitPrefixA | (Integer.parseInt(arrayLongNetmask[1]) << 16);
		int bitPrefixC = bitPrefixB | (Integer.parseInt(arrayLongNetmask[2]) << 8);
		int bitPrefixD = bitPrefixC | (Integer.parseInt(arrayLongNetmask[3]));

		if (bitPrefixD == 0)
			return "0";

		int cont = 0;
		while ((bitPrefixD % 2) == 0) {
			++cont;
			bitPrefixD = bitPrefixD / 2;
		}
		String resultMask = String.valueOf(MAX_MASK - cont);
		return resultMask;
	}

	public static boolean validateAnIpAddressWithRegularExpression(String iPaddress) {
		final Pattern IP_PATTERN =
				Pattern.compile("b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).)"
						+ "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)b");
		return IP_PATTERN.matcher(iPaddress).matches();
	}

}
