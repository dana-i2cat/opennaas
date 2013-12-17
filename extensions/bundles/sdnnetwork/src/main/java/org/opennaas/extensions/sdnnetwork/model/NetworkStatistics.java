package org.opennaas.extensions.sdnnetwork.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.openflowswitch.capability.monitoring.SwitchPortStatistics;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NetworkStatistics {

	private Map<String, SwitchPortStatistics>	switchStatistics;

	public Map<String, SwitchPortStatistics> getSwitchStatistics() {
		return switchStatistics;
	}

	public void setSwitchStatistics(Map<String, SwitchPortStatistics> switchStatistics) {
		this.switchStatistics = switchStatistics;
	}

}
