package org.opennaas.extensions.router.model.wrappers;

import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;

public class SetIpAddressRequest {

	LogicalDevice		logicalDevice;

	IPProtocolEndpoint	ipProtocolEndpoint;

	public LogicalDevice getLogicalDevice() {
		return logicalDevice;
	}

	public void setLogicalDevice(LogicalDevice logicalDevice) {
		this.logicalDevice = logicalDevice;
	}

	public IPProtocolEndpoint getIpProtocolEndpoint() {
		return ipProtocolEndpoint;
	}

	public void setIpProtocolEndpoint(IPProtocolEndpoint ipProtocolEndpoint) {
		this.ipProtocolEndpoint = ipProtocolEndpoint;
	}

}
