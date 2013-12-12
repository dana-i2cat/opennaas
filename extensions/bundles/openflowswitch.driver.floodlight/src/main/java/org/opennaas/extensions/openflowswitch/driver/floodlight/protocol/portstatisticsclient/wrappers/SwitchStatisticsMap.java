package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.wrappers;

import java.util.HashMap;
import java.util.Map;

import org.opennaas.extensions.openflowswitch.capability.monitoring.PortStatistics;

/**
 * Switch statistics map of port statistics wrapper.<br />
 * 
 * Uses a {@link Map} with switch ID as key and another Map as value. The second {@link Map} has port ID as key and {@link PortStatistics} as value.
 * 
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class SwitchStatisticsMap {

	private Map<String, Map<Integer, PortStatistics>>	switchStatisticsMap;

	public SwitchStatisticsMap() {
		this.switchStatisticsMap = new HashMap<String, Map<Integer, PortStatistics>>();
	}

	public Map<String, Map<Integer, PortStatistics>> getSwitchStatisticsMap() {
		return switchStatisticsMap;
	}

	public void addSwitchStatistics(String switchId, Map<Integer, PortStatistics> portStatisticsMap) {
		switchStatisticsMap.put(switchId, portStatisticsMap);
	}

	public void addPortStatisticsForSwitch(String switchId, int port, PortStatistics portStatistics) {
		if (!switchStatisticsMap.containsKey(switchId)) {
			switchStatisticsMap.put(switchId, new HashMap<Integer, PortStatistics>());
		}
		switchStatisticsMap.get(switchId).put(port, portStatistics);
	}
}
