package org.opennaas.extensions.router.capabilities.api.test;

/*
 * #%L
 * OpenNaaS :: Router :: Capabilities :: API
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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.extensions.router.capabilities.api.helper.ChassisAPIHelper;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfaceInfo;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfacesNamesList;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.VLANEndpoint;

/**
 * {@link ChassisAPIHelper} class Unit tests
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class ChassisAPIHelperTest {

	private static final String	IFACE				= "ge-2/0/0";
	private static final int	PORT				= 13;
	private static final String	IFACE_NAME			= IFACE + "." + PORT;
	private static final String	IFACE_VLAN			= "13";
	private static final String	IFACE_STATE			= "OK";
	private static final String	IFACE_DESCRIPTION	= "Test description.";

	private static final String	LR_NAME				= "logical_router";

	private static final String	IFACE_1				= "fe-1/0/0";
	private static final int	PORT_1				= 1;
	private static final String	IFACE_NAME_1		= IFACE_1 + "." + PORT_1;
	private static final String	IFACE_2				= "fe-2/0/0";
	private static final int	PORT_2				= 2;
	private static final String	IFACE_NAME_2		= IFACE_2 + "." + PORT_2;
	private static final String	IFACE_3				= "fe-3/0/0";
	private static final int	PORT_3				= 3;
	private static final String	IFACE_NAME_3		= IFACE_3 + "." + PORT_3;

	@Test
	public void testInterfaceInfo2NetworkPort() {
		InterfaceInfo ii = buildValidInterfaceInfo();
		NetworkPort np = ChassisAPIHelper.interfaceInfo2NetworkPort(ii);

		Assert.assertNotNull("Generated NetworkPort should be not null", np);
		Assert.assertEquals("Name must be " + IFACE, IFACE, np.getName());
		Assert.assertEquals("Port must be " + PORT, PORT, np.getPortNumber());

		List<ProtocolEndpoint> pe = np.getProtocolEndpoint();
		Assert.assertNotNull("Generated NetworkPort must contain ProtocolEndpoints.", pe);
		Assert.assertTrue("Generated NetworkPort must contain just 1 ProtocolEndpoint.", pe.size() == 1);
		Assert.assertTrue("NetworkPort's ProtocolEndpoint must be a VLANEndpoint.", pe.get(0) instanceof VLANEndpoint);

		VLANEndpoint vep = (VLANEndpoint) pe.get(0);
		int vlan = vep.getVlanID();
		Assert.assertEquals("VLAN ID must be " + IFACE_VLAN, IFACE_VLAN, String.valueOf(vlan));

		Assert.assertEquals("Description must be " + IFACE_DESCRIPTION, IFACE_DESCRIPTION, np.getDescription());
	}

	private static InterfaceInfo buildValidInterfaceInfo() {
		InterfaceInfo ii = new InterfaceInfo();
		ii.setName(IFACE_NAME);
		ii.setVlan(IFACE_VLAN);
		ii.setState(IFACE_STATE);
		ii.setDescription(IFACE_DESCRIPTION);

		return ii;
	}

	@Test
	public void testInterfaceName2LogicalPort() {
		LogicalPort lp = ChassisAPIHelper.interfaceName2LogicalPort(IFACE_NAME);

		Assert.assertNotNull("Generated LogicalPort must be not null", lp);

		String name = lp.getName();
		Assert.assertNotNull("Generated LogicalPort name must be not null", name);
		Assert.assertEquals("Name must be " + IFACE_NAME, IFACE_NAME, name);
	}

	@Test
	public void testSubInterfaceName2NetworkPort() {
		NetworkPort np = ChassisAPIHelper.subInterfaceName2NetworkPort(IFACE_NAME);

		Assert.assertNotNull("Generated NetworkPort must be not null", np);

		String name = np.getName();
		Assert.assertNotNull("Generated NetworkPort name must be not null", name);
		Assert.assertEquals("Name must be " + IFACE, IFACE, name);

		int port = np.getPortNumber();
		Assert.assertEquals("Port number must be " + PORT, PORT, port);
	}

	@Test
	public void testLogicalRouter2ComputerSystem() {
		ComputerSystem lr = ChassisAPIHelper.logicalRouter2ComputerSystem(LR_NAME, buildValidInterfacesNamesList());

		Assert.assertNotNull("Generated ComputerSystem must be not null", lr);

		String name = lr.getName();
		Assert.assertNotNull("Generated ComputerSystem name must be not null", name);
		Assert.assertEquals("Name must be " + LR_NAME, LR_NAME, name);
		Assert.assertEquals("Name and element name must be equal " + name + ", " + lr.getElementName(), name, lr.getElementName());

		List<LogicalDevice> ld = lr.getLogicalDevices();
		verify3IfacesAndPorts(ld);
	}

	/*
	 * Verifies that logicalDevicesList contains 3 NetworkPorts with expected names and port numbers
	 */
	private void verify3IfacesAndPorts(List<? extends LogicalDevice> logicalDevicesList) {
		Assert.assertNotNull("Generated ComputerSystem LogicalDevices must be not null", logicalDevicesList);
		Assert.assertEquals("Generated ComputerSystem must contain 3 LogicalDevices", 3, logicalDevicesList.size());
		boolean contains1 = false, contains2 = false, contains3 = false;

		for (LogicalDevice logicalDevice : logicalDevicesList) {
			Assert.assertTrue("Generated ComputerSystem LogicalDevices must be instaces of NetworkPort.", logicalDevice instanceof NetworkPort);

			NetworkPort np = (NetworkPort) logicalDevice;
			if (np.getName().equals(IFACE_1) && np.getPortNumber() == PORT_1) {
				contains1 = true;
			}
			if (np.getName().equals(IFACE_2) && np.getPortNumber() == PORT_2) {
				contains2 = true;
			}
			if (np.getName().equals(IFACE_3) && np.getPortNumber() == PORT_3) {
				contains3 = true;
			}
		}

		Assert.assertTrue("Generated ComputerSystem LogicalDevices must contain " + IFACE_NAME_1 + ", " + IFACE_NAME_2 + " and " + IFACE_NAME_3,
				contains1 && contains2 && contains3);
	}

	private static InterfacesNamesList buildValidInterfacesNamesList() {
		InterfacesNamesList inl = new InterfacesNamesList();

		List<String> interfaces = new ArrayList<String>();
		interfaces.add(IFACE_NAME_1);
		interfaces.add(IFACE_NAME_2);
		interfaces.add(IFACE_NAME_3);

		inl.setInterfaces(interfaces);

		return inl;
	}

	@Test
	public void testInterfaceNameList2NetworkPortList() {
		List<NetworkPort> npl = ChassisAPIHelper.interfaceNameList2NetworkPortList(buildValidInterfacesNamesList());
		verify3IfacesAndPorts(npl);
	}

	@Test
	public void testString2ProtocolIFType() {
		Assert.assertEquals("ProtoclIFType value must be well generated", ProtocolIFType.LAYER_2_VLAN_USING_802_1Q,
				ChassisAPIHelper.string2ProtocolIFType("tagged-ethernet"));
		Assert.assertEquals("ProtoclIFType value must be well generated", ProtocolIFType.UNKNOWN, ChassisAPIHelper.string2ProtocolIFType("none"));
		Assert.assertEquals("ProtoclIFType value must be well generated", ProtocolIFType.OTHER,
				ChassisAPIHelper.string2ProtocolIFType("other-not-listed-value"));
	}

}
