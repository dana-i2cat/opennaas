package org.opennaas.extensions.sdnnetwork.model;

import java.util.HashMap;
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

	public NetworkStatistics() {
		switchStatistics = new HashMap<String, SwitchPortStatistics>();
	}

	public SwitchPortStatistics getSwitchPortStatistic(String switchId) {
		return switchStatistics.get(switchId);
	}

	public void addPortSwitchStatistic(String switchName, SwitchPortStatistics switchPortStatistics) {
		switchStatistics.put(switchName, switchPortStatistics);
	}

	public void removePortSwitchStatistic(String switchId) {
		switchStatistics.remove(switchId);
	}

	public Map<String, SwitchPortStatistics> getSwitchStatistics() {
		return switchStatistics;
	}

	public void setSwitchStatistics(Map<String, SwitchPortStatistics> switchStatistics) {
		this.switchStatistics = switchStatistics;
	}

}
