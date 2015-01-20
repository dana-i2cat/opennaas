package org.opennaas.extensions.genericnetwork.actionsets.internal.portstatistics.actions;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.extensions.genericnetwork.model.driver.DevicePortId;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.PortStatistics;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.SwitchPortStatistics;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Tests for {@link GetPortStatisticsAction}
 * 
 * @author Julio Carlos Barrera
 *
 */
public class GetPortStatisticsActionTest {

	private static final String	SW1		= "sw1";
	private static final String	SW2		= "sw2";
	private static final String	NET1	= "net1";

	@Test
	public void generateSwitchPortStatisticsTest() throws Exception {
		Map<String, SwitchPortStatistics> switchesStatistics = generateSwitchesStatistics();
		BiMap<String, DevicePortId> networkDevicePortIdsMap = generateNetworkDevicePortIdsMap();

		SwitchPortStatistics switchPortStatistics = GetPortStatisticsAction.generateSwitchPortStatistics(switchesStatistics, networkDevicePortIdsMap);

		Assert.assertEquals(10, switchPortStatistics.getStatistics().get(SW1 + "." + "1").getCollisions());
		Assert.assertEquals(20, switchPortStatistics.getStatistics().get(SW1 + "." + "2").getCollisions());
		Assert.assertEquals(3000, switchPortStatistics.getStatistics().get(NET1 + "." + "3").getCollisions());
		Assert.assertEquals(4000, switchPortStatistics.getStatistics().get(NET1 + "." + "4").getCollisions());
	}

	private static Map<String, SwitchPortStatistics> generateSwitchesStatistics() {
		Map<String, SwitchPortStatistics> switchesStatistics = new HashMap<String, SwitchPortStatistics>();

		switchesStatistics.put("openflowswitch:" + SW1, generateSwitchPortStatistics(SW1, 10));
		switchesStatistics.put("openflowswitch:" + SW2, generateSwitchPortStatistics(SW2, 100));
		switchesStatistics.put("genericnetwork:" + NET1, generateSwitchPortStatistics(NET1, 1000));

		return switchesStatistics;
	}

	private static BiMap<String, DevicePortId> generateNetworkDevicePortIdsMap() {
		BiMap<String, DevicePortId> networkDevicePortIdsMap = HashBiMap.<String, DevicePortId> create();

		networkDevicePortIdsMap.put(SW1 + "." + "1", generateDevicePortId("openflowswitch:" + SW1, "1"));
		networkDevicePortIdsMap.put(SW1 + "." + "2", generateDevicePortId("openflowswitch:" + SW1, "2"));
		networkDevicePortIdsMap.put(NET1 + "." + "3", generateDevicePortId("genericnetwork:" + NET1, "3"));
		networkDevicePortIdsMap.put(NET1 + "." + "4", generateDevicePortId("genericnetwork:" + NET1, "4"));

		return networkDevicePortIdsMap;
	}

	private static DevicePortId generateDevicePortId(String deviceId, String devicePortId) {
		DevicePortId dpi = new DevicePortId();
		dpi.setDeviceId(deviceId);
		dpi.setDevicePortId(devicePortId);
		return dpi;
	}

	private static SwitchPortStatistics generateSwitchPortStatistics(String deviceID, int fakeIndex) {
		Map<String, PortStatistics> statistics = new HashMap<String, PortStatistics>();

		// fill only port and collisions in test case
		for (int portId = 1; portId <= 4; portId++) {
			PortStatistics portStatistics = new PortStatistics();
			portStatistics.setPort(portId);
			portStatistics.setCollisions(fakeIndex * portId);

			statistics.put(deviceID + "." + portId, portStatistics);
		}

		SwitchPortStatistics switchPortStatistics = new SwitchPortStatistics();
		switchPortStatistics.setStatistics(statistics);

		return switchPortStatistics;
	}
}
