package org.opennaas.extensions.router.capability.ip.ws.wrapper;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.router.model.LogicalPort;

@XmlRootElement
public class SetInterfaceDescriptionRequest {

	private LogicalPort	iface;

	/**
	 * @return the iface
	 */
	public LogicalPort getIface() {
		return iface;
	}

	/**
	 * @param iface
	 *            the iface to set
	 */
	public void setIface(LogicalPort iface) {
		this.iface = iface;
	}

}
