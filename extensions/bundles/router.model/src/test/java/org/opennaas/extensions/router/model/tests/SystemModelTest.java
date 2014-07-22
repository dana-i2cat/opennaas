package org.opennaas.extensions.router.model.tests;

/*
 * #%L
 * OpenNaaS :: CIM Model
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

import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;

public class SystemModelTest {
	Log				log	= LogFactory.getLog(ManagedElementTest.class);
	ComputerSystem	computerSystem;

	@Before
	public void AddingInterfaces() {
		computerSystem = new ComputerSystem();

		computerSystem.addLogicalDevice(newParamsInterfaceEthernet());
		computerSystem.addLogicalDevice(newParams());
		computerSystem.addLogicalDevice(newParamsInterfaceEthernet());

		Assert.assertNotNull(computerSystem.getLogicalDevices());

		System.out.println(" Size before delete " + computerSystem.getLogicalDevices().size());
		for (LogicalDevice ld : computerSystem.getLogicalDevices()) {
			if (ld instanceof EthernetPort) {
				System.out.println(((EthernetPort) ld).getPortNumber());
			} else {
				NetworkPort n = (NetworkPort) ld;
				System.out.println(n.getPortNumber());
			}
		}
	}

	@Test
	public void removingInterfaces() {
		computerSystem.removeAllLogicalDeviceByType(EthernetPort.class);

		List<LogicalDevice> logicaldevices = computerSystem.getLogicalDevices();
		// Assert.assertTrue(logicaldevices.isEmpty());
		System.out.println(" Size after delete " + logicaldevices.size());
		for (LogicalDevice ld : logicaldevices) {
			if (ld instanceof EthernetPort) {
				System.out.println(((EthernetPort) ld).getPortNumber());
			} else {
				NetworkPort n = (NetworkPort) ld;
				System.out.println(n.getPortNumber());
			}
		}
	}

	public LogicalDevice newParamsInterfaceEthernet() {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName("fe-0/3/2");
		eth.setPortNumber(100);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		// System.out.println(eth.getLinkTechnology().toString());
		return eth;
	}

	public LogicalDevice newParams() {
		NetworkPort eth = new NetworkPort();
		eth.setPortNumber(25);
		return eth;
	}

}
