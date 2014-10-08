package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.wrappers;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Floodlight driver v0.90
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.HashMap;
import java.util.Map;

import org.opennaas.extensions.openflowswitch.capability.portstatistics.PortStatistics;

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
