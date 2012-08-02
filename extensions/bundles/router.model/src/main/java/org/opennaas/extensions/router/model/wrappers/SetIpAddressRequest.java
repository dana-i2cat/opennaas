package org.opennaas.extensions.router.model.wrappers;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;

@XmlRootElement
public class SetIpAddressRequest {

	private LogicalDevice		logicalDevice;

	private IPProtocolEndpoint	ipProtocolEndpoint;

	/**
	 * @return the logicalDevice
	 */
	public LogicalDevice getLogicalDevice() {
		return logicalDevice;
	}

	/**
	 * @param logicalDevice
	 *            the logicalDevice to set
	 */
	public void setLogicalDevice(LogicalDevice logicalDevice) {
		this.logicalDevice = logicalDevice;
	}

	/**
	 * @return the ipProtocolEndpoint
	 */
	public IPProtocolEndpoint getIpProtocolEndpoint() {
		return ipProtocolEndpoint;
	}

	/**
	 * @param ipProtocolEndpoint
	 *            the ipProtocolEndpoint to set
	 */
	public void setIpProtocolEndpoint(IPProtocolEndpoint ipProtocolEndpoint) {
		this.ipProtocolEndpoint = ipProtocolEndpoint;
	}

}
