package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.wrappers;

import java.util.HashMap;
import java.util.Map;

/**
 * Switch statistics map of port statistics wrapper
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class SwitchStatisticsMap {

	private Map<String, HashMap<Integer, PortStatistics>>	switchStatisticsMap;

	public SwitchStatisticsMap() {
		this.switchStatisticsMap = new HashMap<String, HashMap<Integer, PortStatistics>>();
	}

	public Map<String, HashMap<Integer, PortStatistics>> getSwitchStatisticsMap() {
		return switchStatisticsMap;
	}

	public void addSwitchStatistics(String switchId, HashMap<Integer, PortStatistics> portStatisticsMap) {
		switchStatisticsMap.put(switchId, portStatisticsMap);
	}

	public void addPortStatisticsForSwitch(String switchId, int port, PortStatistics portStatistics) {
		if (!switchStatisticsMap.containsKey(switchId)) {
			switchStatisticsMap.put(switchId, new HashMap<Integer, PortStatistics>());
		}
		switchStatisticsMap.get(switchId).put(port, portStatistics);
	}
}
