package org.opennaas.extensions.network.capability.basic.tests;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.extensions.network.capability.basic.mappers.Cim2NdlMapper;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.technology.ethernet.EthernetLayer;
import org.opennaas.extensions.network.model.technology.ethernet.TaggedEthernetLayer;
import org.opennaas.extensions.network.model.technology.ip.IPLayer;
import org.opennaas.extensions.network.model.topology.ConnectionPoint;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.topology.NetworkElement;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.VLANEndpoint;

public class Cim2NdlMapperTest {

	/**
	 * Create cim model (Router with multiples ifaces with Vlans and IPs and lt tunnels).<br/>
	 * Pass it to ndl <br/>
	 * Check device <br/>
	 * Check each endpoint is represented <br/>
	 * Check links are represented <br/>
	 */
	@Test
	public void addManagedElementToModelTest() {
		NetworkModel networkModel = new NetworkModel();
		System model = createTestRouterModel();
		List<NetworkElement> createdElements = Cim2NdlMapper.addModelToNetworkModel(model, networkModel, model.getName());

		Assert.assertFalse(networkModel.getNetworkElements().isEmpty());
		Assert.assertTrue(networkModel.getNetworkElements().containsAll(createdElements));
		Assert.assertEquals(networkModel.getNetworkElements().size(), createdElements.size());
		Assert.assertTrue(checkDevice(createdElements));
		Assert.assertTrue(checkEndPoints(createdElements));
		Assert.assertTrue(checkLinks(createdElements));
	}

	/**
	 * There are 3 bidirectional links <br>
	 * Each link has a reference to two interfaces Each interface in a link <br>
	 * Has a reference to that link Each link is in the same layer than its interfaces <br>
	 */
	private boolean checkLinks(List<NetworkElement> createdElements) {
		List<Link> links = NetworkModelHelper.getLinks(createdElements);

		Assert.assertEquals(3, links.size());
		int taggedEthLinksCount = 0;
		int ethLinksCount = 0;
		for (Link link : links) {
			Assert.assertNotNull(link.getSource());
			Assert.assertNotNull(link.getSink());

			Assert.assertNotNull(link.getSource().getLinkTo());
			Assert.assertNotNull(link.getSink().getLinkTo());
			Assert.assertTrue(link.getSource().getLinkTo().equals(link));
			Assert.assertTrue(link.getSink().getLinkTo().equals(link));

			Assert.assertTrue(link.getLayer().equals(link.getSource().getLayer()));
			Assert.assertTrue(link.getLayer().equals(link.getSink().getLayer()));

			Assert.assertTrue(link.isBidirectional());

			if (link.getLayer() instanceof TaggedEthernetLayer) {
				taggedEthLinksCount++;
			} else if (link.getLayer() instanceof EthernetLayer) {
				ethLinksCount++;
			}
		}
		Assert.assertEquals(1, taggedEthLinksCount);
		Assert.assertEquals(2, ethLinksCount);

		return true;
	}

	/**
	 * There are 9 Interfaces with EthLayer <br/>
	 * There are 10 Interfaces with TaggedEthLayer <br/>
	 * There are 05 Interfaces with IPLayer <br/>
	 * 
	 * @param networkElements
	 * @return true if pass the test
	 */
	private boolean checkEndPoints(List<NetworkElement> networkElements) {
		List<ConnectionPoint> connectionPoints = NetworkModelHelper.getConnectionPoints(networkElements);
		int ethIfaceCount = 0;
		int vlanIfaceCount = 0;
		int ipIfaceCount = 0;

		// Physical and pure Interfaces
		ethIfaceCount = checkEthernetInterfaces(connectionPoints);

		// Tagged (VLAN) Interfaces
		vlanIfaceCount = checkVLANInterfaces(connectionPoints);

		// IP Interfaces
		ipIfaceCount = checkIPInterfaces(connectionPoints);

		// check number of interfaces is correct
		Assert.assertEquals(ethIfaceCount, 14);
		Assert.assertEquals(vlanIfaceCount, 10);
		Assert.assertEquals(ipIfaceCount, 5);

		return true;
	}

	/**
	 * Check pure and physical interfaces
	 * 
	 * @param connectionPoints
	 * @return number of pure and physical interfaces
	 */
	private int checkEthernetInterfaces(List<ConnectionPoint> connectionPoints) {
		int ethIfaceCount = 0;

		// Pure Ethernet Interfaces
		boolean fe_0_0_2_0Found = false;
		boolean fe_0_0_3_0Found = false;
		boolean lt_0_2_1_1Found = false;
		boolean lt_0_2_1_2Found = false;

		for (ConnectionPoint connectionPoint : connectionPoints) {
			if (connectionPoint instanceof Interface) {
				if (connectionPoint.getLayer() instanceof EthernetLayer) {
					fe_0_0_2_0Found = fe_0_0_2_0Found || connectionPoint.getName().endsWith("fe-0/0/2.0");
					fe_0_0_3_0Found = fe_0_0_3_0Found || connectionPoint.getName().endsWith("fe-0/0/3.0");
					lt_0_2_1_1Found = lt_0_2_1_1Found || connectionPoint.getName().endsWith("lt-0/2/1.1");
					lt_0_2_1_2Found = lt_0_2_1_2Found || connectionPoint.getName().endsWith("lt-0/2/1.2");

					ethIfaceCount++;
				}
			}
		}

		// Pure Ethernet Interfaces
		Assert.assertTrue(fe_0_0_2_0Found);
		Assert.assertTrue(fe_0_0_3_0Found);
		Assert.assertTrue(lt_0_2_1_1Found);
		Assert.assertTrue(lt_0_2_1_2Found);

		return ethIfaceCount;
	}

	/**
	 * @param connectionPoints
	 * @return
	 */
	private int checkVLANInterfaces(List<ConnectionPoint> connectionPoints) {
		int vlanIfaceCount = 0;

		// Tagged Ethernet Interfaces
		boolean fe_0_0_1_1Found = false;
		boolean fe_0_0_1_2Found = false;
		boolean fe_0_0_1_3Found = false;
		boolean fe_0_0_1_4Found = false;
		boolean fe_0_1_1_1Found = false;
		boolean fe_0_1_1_2Found = false;
		boolean fe_0_1_1_3Found = false;
		boolean fe_0_1_1_4Found = false;
		boolean lt_0_2_1_3Found = false;
		boolean lt_0_2_1_4Found = false;

		for (ConnectionPoint connectionPoint : connectionPoints) {
			if (connectionPoint.getLayer() instanceof TaggedEthernetLayer) {
				fe_0_0_1_1Found = fe_0_0_1_1Found || connectionPoint.getName().endsWith("fe-0/0/1.1");
				fe_0_0_1_2Found = fe_0_0_1_2Found || connectionPoint.getName().endsWith("fe-0/0/1.2");
				fe_0_0_1_3Found = fe_0_0_1_3Found || connectionPoint.getName().endsWith("fe-0/0/1.3");
				fe_0_0_1_4Found = fe_0_0_1_4Found || connectionPoint.getName().endsWith("fe-0/0/1.4");
				fe_0_1_1_1Found = fe_0_1_1_1Found || connectionPoint.getName().endsWith("fe-0/1/1.1");
				fe_0_1_1_2Found = fe_0_1_1_2Found || connectionPoint.getName().endsWith("fe-0/1/1.2");
				fe_0_1_1_3Found = fe_0_1_1_3Found || connectionPoint.getName().endsWith("fe-0/1/1.3");
				fe_0_1_1_4Found = fe_0_1_1_4Found || connectionPoint.getName().endsWith("fe-0/1/1.4");
				lt_0_2_1_3Found = lt_0_2_1_3Found || connectionPoint.getName().endsWith("lt-0/2/1.3");
				lt_0_2_1_4Found = lt_0_2_1_4Found || connectionPoint.getName().endsWith("lt-0/2/1.4");

				Assert.assertNotNull(connectionPoint.getServerInterface());
				Assert.assertNotNull(connectionPoint.getServerInterface().getLayer());
				Assert.assertTrue("any interface in TaggedEthLayer has a serverInterface in EthLayer",
						connectionPoint.getServerInterface().getLayer() instanceof EthernetLayer);
				Assert.assertEquals(connectionPoint.getName()
						, connectionPoint.getServerInterface().getName());

				vlanIfaceCount++;
			}
		}

		Assert.assertTrue(fe_0_0_1_1Found);
		Assert.assertTrue(fe_0_0_1_2Found);
		Assert.assertTrue(fe_0_0_1_3Found);
		Assert.assertTrue(fe_0_0_1_4Found);
		Assert.assertTrue(fe_0_1_1_1Found);
		Assert.assertTrue(fe_0_1_1_2Found);
		Assert.assertTrue(fe_0_1_1_3Found);
		Assert.assertTrue(fe_0_1_1_4Found);
		Assert.assertTrue(lt_0_2_1_3Found);
		Assert.assertTrue(lt_0_2_1_4Found);

		return vlanIfaceCount;
	}

	/**
	 * @param connectionPoints
	 * @return
	 */
	private int checkIPInterfaces(List<ConnectionPoint> connectionPoints) {
		int ipIfaceCount = 0;

		// IP Interfaces
		boolean ip192_168_11_11Found = false;
		boolean ip192_168_0_1Found = false;
		boolean ip192_168_0_3Found = false;
		boolean ip10_0_0_1Found = false;
		boolean ip10_0_0_3Found = false;

		for (ConnectionPoint connectionPoint : connectionPoints) {
			if (connectionPoint.getLayer() instanceof IPLayer) {
				ip192_168_11_11Found = ip192_168_11_11Found || connectionPoint.getName().endsWith("192.168.11.11");
				ip192_168_0_1Found = ip192_168_0_1Found || connectionPoint.getName().endsWith("192.168.0.1");
				ip192_168_0_3Found = ip192_168_0_3Found || connectionPoint.getName().endsWith("192.168.0.3");
				ip10_0_0_1Found = ip10_0_0_1Found || connectionPoint.getName().endsWith("10.0.0.1");
				ip10_0_0_3Found = ip10_0_0_3Found || connectionPoint.getName().endsWith("10.0.0.3");

				Assert.assertNotNull(connectionPoint.getServerInterface());
				Assert.assertNotNull(connectionPoint.getServerInterface().getLayer());
				Assert.assertTrue("any interface in IPLayer has a serverInterface in an other layer",
						connectionPoint.getServerInterface().getLayer() instanceof EthernetLayer ||
								connectionPoint.getServerInterface().getLayer() instanceof TaggedEthernetLayer);

				// check server interfaces chain
				if (connectionPoint.getName().equals("192.168.11.11")) {
					Assert.assertNotNull(connectionPoint.getServerInterface().getName());
					Assert.assertTrue(connectionPoint.getServerInterface().getName().endsWith("fe-0/0/3.0"));
					Assert.assertTrue(connectionPoint.getServerInterface().getLayer() instanceof EthernetLayer);
				} else if (connectionPoint.getName().contains("192.168.0.")) {
					Assert.assertNotNull(connectionPoint.getServerInterface().getName());
					Assert.assertTrue(connectionPoint.getServerInterface().getName().contains("fe-0/0/1"));
					Assert.assertTrue(connectionPoint.getServerInterface().getLayer() instanceof TaggedEthernetLayer);
				} else if (connectionPoint.getName().contains("10.0.0.")) {
					Assert.assertNotNull(connectionPoint.getServerInterface().getName());
					Assert.assertTrue(connectionPoint.getServerInterface().getName().contains("fe-0/1/1"));
					Assert.assertTrue(connectionPoint.getServerInterface().getLayer() instanceof TaggedEthernetLayer);
				}

				ipIfaceCount++;
			}
		}

		// IP Interfaces
		Assert.assertTrue(ip192_168_11_11Found);
		Assert.assertTrue(ip192_168_0_1Found);
		Assert.assertTrue(ip192_168_0_3Found);
		Assert.assertTrue(ip10_0_0_1Found);
		Assert.assertTrue(ip10_0_0_3Found);

		return ipIfaceCount;
	}

	private boolean checkDevice(List<NetworkElement> networkElements) {
		int devCount = 0;
		for (NetworkElement elem : networkElements) {
			if (elem instanceof Device) {

				Assert.assertTrue(((Device) elem).getName().equals("TestRouter"));

				// check device contains all created interfaces
				for (NetworkElement otherElem : networkElements) {
					if (otherElem instanceof ConnectionPoint)
						Assert.assertTrue("Any created interface is part of device", ((Device) elem).getInterfaces().contains(otherElem));
				}
				devCount++;
			}
		}

		Assert.assertEquals("A single device has been created. DevicesNum = " + devCount, 1, devCount);

		return true;
	}

	/**
	 * @return A test router to test with:
	 * 
	 */
	private System createTestRouterModel() {
		ComputerSystem router = new ComputerSystem();
		router.setName("TestRouter");
		router.setDescription("Router for testing cim2NdlMapper");

		EthernetPort ethPort = new EthernetPort();
		ethPort.setDescription("Eth port in testing router");
		ethPort.setName("fe-0/0/2");
		ethPort.setPortNumber(0);
		router.addLogicalDevice(ethPort);

		ethPort = new EthernetPort();
		ethPort.setDescription("Eth port in testing router");
		ethPort.setName("fe-0/0/3");
		ethPort.setPortNumber(0);
		IPProtocolEndpoint ipEndPoint = new IPProtocolEndpoint();
		ipEndPoint.setIPv4Address("192.168.11.11");
		ethPort.addProtocolEndpoint(ipEndPoint);
		router.addLogicalDevice(ethPort);

		// 4 eth
		for (int i = 1; i <= 4; i++) {
			ethPort = new EthernetPort();
			ethPort.setDescription("Eth port " + i + " in testing router");
			ethPort.setName("fe-0/0/1");
			ethPort.setPortNumber(i);
			router.addLogicalDevice(ethPort);

			VLANEndpoint endPoint = new VLANEndpoint();
			endPoint.setVlanID(i + 20);
			ethPort.addProtocolEndpoint(endPoint);
			// .1, .3 have IP address
			if (i % 2 == 1) {
				ipEndPoint = new IPProtocolEndpoint();
				ipEndPoint.setIPv4Address("192.168.0." + i);
				ethPort.addProtocolEndpoint(ipEndPoint);
			}

		}
		// 4 eth
		for (int i = 1; i <= 4; i++) {
			ethPort = new EthernetPort();
			ethPort.setDescription("Eth port " + i + " in testing router");
			ethPort.setName("fe-0/1/1");
			ethPort.setPortNumber(i);
			router.addLogicalDevice(ethPort);

			VLANEndpoint endPoint = new VLANEndpoint();
			endPoint.setVlanID(i + 40);
			ethPort.addProtocolEndpoint(endPoint);

			// .1, .3 have IP address
			if (i % 2 == 1) {
				ipEndPoint = new IPProtocolEndpoint();
				ipEndPoint.setIPv4Address("10.0.0." + i);
				ethPort.addProtocolEndpoint(ipEndPoint);
			}
		}

		// 4 lts
		for (int i = 1; i <= 4; i++) {
			LogicalTunnelPort ltPort = new LogicalTunnelPort();
			ltPort.setDescription("lt port " + i + " in testing router");
			ltPort.setName("lt-0/2/1");
			ltPort.setPortNumber(i);
			router.addLogicalDevice(ltPort);

			// set peerUnits (1-2, 3-4)
			if (i % 2 == 0) {
				ltPort.setPeer_unit(i - 1);
			} else {
				ltPort.setPeer_unit(i + 1);
			}
			// lts 3,4 use vlan
			if (i > 2) {
				VLANEndpoint endPoint = new VLANEndpoint();
				endPoint.setVlanID(50 + i);
				ltPort.addProtocolEndpoint(endPoint);
			}
		}

		return router;
	}
}
