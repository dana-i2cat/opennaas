package org.opennaas.extensions.router.model.tests;

import java.util.List;

import junit.framework.Assert;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.VLANEndpoint;
import org.opennaas.extensions.router.model.mappers.Cim2NdlMapper;
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

import org.junit.Test;

public class Cim2NdlMapperTest {

	@Test
	public void addManagedElementToModelTest() {
		// create cim model (Router with multiples ifaces with Vlans and IPs and lt tunnels).
		// pass it to ndl
		// check device
		// check each endpoint is represented
		// check links are represented

		NetworkModel networkModel = new NetworkModel();
		int elemCount = networkModel.getNetworkElements().size();

		System model = createTestRouterModel();

		List<NetworkElement> createdElements = Cim2NdlMapper.addModelToNetworkModel(model, networkModel, model.getName());

		Assert.assertFalse(networkModel.getNetworkElements().isEmpty());

		Assert.assertTrue(networkModel.getNetworkElements().containsAll(createdElements));
		// nothing has been removed
		Assert.assertTrue(networkModel.getNetworkElements().size() == createdElements.size() + elemCount);

		Assert.assertTrue(checkDevice(createdElements));
		Assert.assertTrue(checkEndPoints(createdElements));
		Assert.assertTrue(checkLinks(createdElements));

	}

	private boolean checkLinks(List<NetworkElement> createdElements) {
		// there are 3 bidirectional links (2 at EthernetLayer, 1 at TaggedEthLayer)
		// each link has a reference to two interfaces
		// each interface in a link has a reference to that link
		// each link is in the same layer than its interfaces

		List<Link> links = NetworkModelHelper.getLinks(createdElements);

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
		}

		Assert.assertTrue(links.size() == 3);

		return true;
	}

	private boolean checkEndPoints(List<NetworkElement> networkElements) {
		// there are 14 Interfaces with EthLayer
		// there are 10 Interfaces with TaggedEthLayer
		// there are 05 Interfaces with IPLayer
		// any interface with TaggedEthLayer should have a serverInterface having EthLayer
		// any interface with IPLayer should have a serverInterface in an other layer.

		List<ConnectionPoint> connectionPoints = NetworkModelHelper.getConnectionPoints(networkElements);

		int ethIfaceCount = 0;
		int vlanIfaceCount = 0;
		int ipIfaceCount = 0;
		int unknownIfaceCount = 0;

		boolean fe_0_0_2_0Found = false;
		boolean fe_0_0_3_0Found = false;
		boolean fe_0_0_1_X_ethFound = false;
		boolean fe_0_0_1_X_vlanFound = false;
		boolean fe_0_1_1_X_ethFound = false;
		boolean fe_0_1_1_X_vlanFound = false;
		boolean lt_0_2_1_X_ethFound = false;
		boolean lt_0_2_1_X_vlanFound = false;

		boolean ip192_168_11_11Found = false;
		boolean ip192_168_0_1Found = false;
		boolean ip192_168_0_3Found = false;
		boolean ip10_0_0_1Found = false;
		boolean ip10_0_0_3Found = false;

		for (ConnectionPoint connectionPoint : connectionPoints) {

			if (connectionPoint instanceof Interface) {
				if (connectionPoint.getLayer() instanceof EthernetLayer) {

					fe_0_0_2_0Found = fe_0_0_2_0Found || connectionPoint.getName().endsWith("fe-0/0/2.0");
					fe_0_0_3_0Found = fe_0_0_3_0Found || connectionPoint.getName().endsWith("fe-0/0/3.0");
					fe_0_0_1_X_ethFound = fe_0_0_1_X_ethFound || connectionPoint.getName().contains("fe-0/0/1.");
					fe_0_1_1_X_ethFound = fe_0_1_1_X_ethFound || connectionPoint.getName().contains("fe-0/1/1.");
					lt_0_2_1_X_ethFound = lt_0_2_1_X_ethFound || connectionPoint.getName().contains("lt-0/2/1.");

					ethIfaceCount++;

				} else if (connectionPoint.getLayer() instanceof TaggedEthernetLayer) {

					fe_0_0_1_X_vlanFound = fe_0_0_1_X_vlanFound || connectionPoint.getName().contains("fe-0/0/1.");
					fe_0_1_1_X_vlanFound = fe_0_1_1_X_vlanFound || connectionPoint.getName().contains("fe-0/1/1.");
					lt_0_2_1_X_vlanFound = lt_0_2_1_X_vlanFound || connectionPoint.getName().contains("lt-0/2/1.");

					Assert.assertNotNull(connectionPoint.getServerInterface());
					Assert.assertNotNull(connectionPoint.getServerInterface().getLayer());
					Assert.assertTrue("any interface in TaggedEthLayer has a serverInterface in EthLayer",
							connectionPoint.getServerInterface().getLayer() instanceof EthernetLayer);

					vlanIfaceCount++;

				} else if (connectionPoint.getLayer() instanceof IPLayer) {

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

				} else {
					unknownIfaceCount++;
				}
			}
		}

		// check required interfces has been created
		Assert.assertTrue(fe_0_0_2_0Found);
		Assert.assertTrue(fe_0_0_3_0Found);
		Assert.assertTrue(fe_0_0_1_X_ethFound);
		Assert.assertTrue(fe_0_0_1_X_vlanFound);
		Assert.assertTrue(fe_0_1_1_X_ethFound);
		Assert.assertTrue(fe_0_1_1_X_vlanFound);
		Assert.assertTrue(lt_0_2_1_X_ethFound);
		Assert.assertTrue(lt_0_2_1_X_vlanFound);

		Assert.assertTrue(ip192_168_11_11Found);
		Assert.assertTrue(ip192_168_0_1Found);
		Assert.assertTrue(ip192_168_0_3Found);
		Assert.assertTrue(ip10_0_0_1Found);
		Assert.assertTrue(ip10_0_0_3Found);

		// check number of interfaces is correct
		Assert.assertTrue(ethIfaceCount == 14);
		Assert.assertTrue(vlanIfaceCount == 10);
		Assert.assertTrue(ipIfaceCount == 5);
		Assert.assertTrue(unknownIfaceCount == 0);

		return true;
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

		Assert.assertTrue("A single device has been created. DevicesNum=" + devCount, devCount == 1);

		return true;
	}

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
			// lts 2,3 use vlan
			if (i > 2) {
				VLANEndpoint endPoint = new VLANEndpoint();
				endPoint.setVlanID(50 + i);
				ltPort.addProtocolEndpoint(endPoint);
			}
		}

		return router;
	}

}
