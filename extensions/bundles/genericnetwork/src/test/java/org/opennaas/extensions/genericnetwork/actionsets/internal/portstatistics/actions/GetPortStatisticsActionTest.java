package org.opennaas.extensions.genericnetwork.actionsets.internal.portstatistics.actions;

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

		Assert.assertEquals(10, switchPortStatistics.getStatistics().get("1").getCollisions());
		Assert.assertEquals(20, switchPortStatistics.getStatistics().get("2").getCollisions());
		Assert.assertEquals(3000, switchPortStatistics.getStatistics().get("3").getCollisions());
		Assert.assertEquals(4000, switchPortStatistics.getStatistics().get("4").getCollisions());
	}

	private static Map<String, SwitchPortStatistics> generateSwitchesStatistics() {
		Map<String, SwitchPortStatistics> switchesStatistics = new HashMap<String, SwitchPortStatistics>();

		switchesStatistics.put(SW1, generateSwitchPortStatistics(SW1, 10));
		switchesStatistics.put(SW2, generateSwitchPortStatistics(SW2, 100));
		switchesStatistics.put(NET1, generateSwitchPortStatistics(NET1, 1000));

		return switchesStatistics;
	}

	private static BiMap<String, DevicePortId> generateNetworkDevicePortIdsMap() {
		BiMap<String, DevicePortId> networkDevicePortIdsMap = HashBiMap.<String, DevicePortId> create();

		networkDevicePortIdsMap.put("1", generateDevicePortId(SW1, "1"));
		networkDevicePortIdsMap.put("2", generateDevicePortId(SW1, "2"));
		networkDevicePortIdsMap.put("3", generateDevicePortId(NET1, "3"));
		networkDevicePortIdsMap.put("4", generateDevicePortId(NET1, "4"));

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

			statistics.put("" + portId, portStatistics);
		}

		SwitchPortStatistics switchPortStatistics = new SwitchPortStatistics();
		switchPortStatistics.setStatistics(statistics);

		return switchPortStatistics;
	}
}
