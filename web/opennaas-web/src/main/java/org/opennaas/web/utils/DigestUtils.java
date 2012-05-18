/**
 * 
 */
package org.opennaas.web.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Jordi
 * 
 */
public class DigestUtils {

	/**
	 * @param value1
	 *            , string value
	 * @param value2
	 *            , digest value
	 * @return true if digest of value1 is equals to value2
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public static boolean compareDigestValues(String value1, String value2)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] aValue = getHash(value1.getBytes("UTF8"));
		return convertByteToHex(aValue).equals(value2);
	}

	/**
	 * @param content
	 * @return hash value
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] getHash(byte[] content) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		return md.digest(content);
	}

	/**
	 * @param byteData
	 *            , byte array
	 * @return String hexadecimal from byte array
	 */
	public static String convertByteToHex(byte[] byteData) {
		// convert the byte to hex format method 1
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
}
