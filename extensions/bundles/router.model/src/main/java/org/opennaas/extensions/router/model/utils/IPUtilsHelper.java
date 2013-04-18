package org.opennaas.extensions.router.model.utils;

import java.io.IOException;
import java.util.regex.Pattern;

import com.google.common.net.InetAddresses;

/**
 * It is used to parse different messages
 * 
 * @author Carlos Baez Ruiz
 */
public class IPUtilsHelper {

	private static final String	IP_ADDRESS_PATTERN	=
															"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
																	"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
																	"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
																	"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public static long ipv4StringToLong(String ip) throws IOException {
		return ModelHelper.ipv4StringToLong(ip);
	}

	public static String ipv4LongToString(long ip) throws IOException {
		return ModelHelper.ipv4LongToString(ip);
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

	public static String ipAddressAndMaskToComposedIPAddress(String ipAddress, String mask) {
		return ipAddress + "/" + parseLongToShortIpv4NetMask(mask);
	}

	public static String[] composedIPAddressToIPAddressAndMask(String composedIPAddress) {
		String[] parts = composedIPAddress.split("\\/");
		if (parts.length > 1) {
			parts[1] = parseShortToLongIpv4NetMask(parts[1]);
		}
		return parts;
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

	/**
	 * Check for a pattern [0..255].[0..255].[0..255].[0..255]
	 * 
	 * @param ipAddress
	 * @return false if don't find the pattern
	 */
	public static boolean validateIpAddressPattern(String ipAddress) {
		return ipAddress.matches(IP_ADDRESS_PATTERN);
	}

	public static boolean validateAnIpAddressWithRegularExpression(String iPaddress) {
		final Pattern IP_PATTERN =
				Pattern.compile("b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).)"
						+ "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)b");
		return IP_PATTERN.matcher(iPaddress).matches();
	}

	public static boolean validateIPv6Address(String ipAddress) {

		return InetAddresses.isInetAddress(ipAddress);
	}

	public static boolean isIPv4ValidAddress(String ipAddress) {

		if (ipAddress.split("/").length != 2)
			return false;

		String ip = getAddressFromIP(ipAddress);
		String mask = getPrefixFromIp(ipAddress);

		boolean validIP = validateIpAddressPattern(ip);
		boolean validMask = validateSubnetMask(mask);

		return (validIP && validMask);
	}

	public static boolean validateSubnetMask(String mask) {

		int maskSize = Integer.valueOf(mask);
		if ((maskSize < 0) || (maskSize > 32))
			return false;

		return true;
	}

	public static boolean isIPv6ValidAddress(String ipAddress) {

		if (ipAddress.split("/").length != 2)
			return false;

		String ip = getAddressFromIP(ipAddress);
		String preffix = getPrefixFromIp(ipAddress);

		boolean validIP = validateIPv6Address(ip);
		boolean validPrefix = validateIPv6Prefix(Short.valueOf(preffix));

		return (validIP && validPrefix);
	}

	public static boolean validateIPv6Prefix(Short prefixLength) {
		if (prefixLength < 0 || prefixLength > 128)
			return false;
		return true;
	}

	public static String getAddressFromIP(String ip) {
		return ip.split("/")[0];
	}

	public static String getPrefixFromIp(String ip) {
		return ip.split("/")[1];
	}

}
