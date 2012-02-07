package net.i2cat.mantychore.model.utils;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.System;

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

}
