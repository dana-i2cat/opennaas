package org.opennaas.extensions.router.model.wrappers;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.OSPFArea;

/**
 * @author Jordi
 */
@XmlRootElement
public class AddInterfacesInOSPFAreaRequest {

	private List<LogicalPort>	interfaces;

	private OSPFArea			ospfArea;

	/**
	 * @return the interfaces
	 */
	public List<LogicalPort> getInterfaces() {
		return interfaces;
	}

	/**
	 * @param interfaces
	 *            the interfaces to set
	 */
	public void setInterfaces(List<LogicalPort> interfaces) {
		this.interfaces = interfaces;
	}

	/**
	 * @return the ospfArea
	 */
	public OSPFArea getOspfArea() {
		return ospfArea;
	}

	/**
	 * @param ospfArea
	 *            the ospfArea to set
	 */
	public void setOspfArea(OSPFArea ospfArea) {
		this.ospfArea = ospfArea;
	}

}
