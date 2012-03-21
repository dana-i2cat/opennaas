package net.i2cat.mantychore.model.tests;

import java.util.List;

import junit.framework.Assert;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.NetworkPort;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

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
