package org.opennaas.extensions.router.model.utils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.System;

public class ModelHelper {

	public static List<NetworkPort> getInterfaces(System system) {
		List<NetworkPort> ports = new ArrayList<NetworkPort>();
		for (LogicalDevice dev : system.getLogicalDevices()) {
			if (dev instanceof NetworkPort) {
				ports.add((NetworkPort) dev);
			}
		}
		return ports;
	}

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

}
