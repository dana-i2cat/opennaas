package org.opennaas.extensions.router.junos.actionssets.actions.test;

import java.util.HashMap;

import org.opennaas.core.protocols.sessionmanager.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.mock.MockEventManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.protocols.netconf.NetconfProtocolSessionFactory;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.GRETunnelConfiguration;
import org.opennaas.extensions.router.model.GRETunnelEndpoint;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;

/**
 * @author ?
 * @author Julio Carlos Barrera
 * 
 */
public class ActionTestHelper {

	String	resourceId	= "RandomDevice";

	public ProtocolSessionManager getProtocolSessionManager() {

		ProtocolManager protocolManager = new ProtocolManager();
		ProtocolSessionManager protocolSessionManager = null;
		try {
			protocolSessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceId);
			protocolSessionManager.setEventManager(new MockEventManager());
			ProtocolSessionContext netconfContext = newSessionContextNetconf();
			protocolManager.sessionFactoryAdded(new NetconfProtocolSessionFactory(), new HashMap<String, String>() {
				{
					put(ProtocolSessionContext.PROTOCOL, "netconf");
				}
			});
			protocolSessionManager.registerContext(netconfContext);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return protocolSessionManager;
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";

		}

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		protocolSessionContext.addParameter(ProtocolSessionContext.AUTH_TYPE, "password");
		// ADDED
		return protocolSessionContext;

	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	public Object newParamsInterfaceEthernet() {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName("fe-0/3/2");
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		return eth;
	}

	public Object newParamsGRETunnelService() {
		GRETunnelService greService = new GRETunnelService();
		greService.setName("gre-0/1/0");
		return greService;
	}

	public Object newParamsGRETunnelServiceWithEndpoint() {
		GRETunnelService greService = new GRETunnelService();
		greService.setName("gre-0/1/0");

		GRETunnelConfiguration greConfig = new GRETunnelConfiguration();
		greConfig.setSourceAddress("147.12.61.43");
		greConfig.setDestinationAddress("193.23.1.12");
		greService.setGRETunnelConfiguration(greConfig);

		GRETunnelEndpoint gE = new GRETunnelEndpoint();
		gE.setIPv4Address("10.10.10.1/24");
		greService.addProtocolEndpoint(gE);

		return greService;
	}

	public static VRRPGroup newParamsVRRPGroupWithOneEndpoint() {
		// VRRPGroup
		VRRPGroup vrrpGroup = new VRRPGroup();
		vrrpGroup.setVrrpName(201);
		vrrpGroup.setVirtualIPAddress("192.168.100.1");

		// VRRPProtocolEndpoint 1
		VRRPProtocolEndpoint vrrProtocolEndpoint1 = new VRRPProtocolEndpoint();
		vrrProtocolEndpoint1.setPriority(100);
		vrrProtocolEndpoint1.setService(vrrpGroup);

		IPProtocolEndpoint ipProtocolEndpoint1 = new IPProtocolEndpoint();
		ipProtocolEndpoint1.setIPv4Address("192.168.1.1");
		ipProtocolEndpoint1.setSubnetMask("255.255.255.0");
		vrrProtocolEndpoint1.bindServiceAccessPoint(ipProtocolEndpoint1);

		EthernetPort eth1 = new EthernetPort();
		eth1.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth1.setName("fe-0/3/2");
		ipProtocolEndpoint1.addLogiaclPort(eth1);

		return vrrpGroup;
	}

	public static VRRPGroup newParamsVRRPGroupWithTwoEndpoints() {
		VRRPGroup vrrpGroup = newParamsVRRPGroupWithOneEndpoint();
		// VRRPProtocolEndpoint 2
		VRRPProtocolEndpoint vrrProtocolEndpoint2 = new VRRPProtocolEndpoint();
		vrrProtocolEndpoint2.setPriority(200);
		vrrProtocolEndpoint2.setService(vrrpGroup);

		IPProtocolEndpoint ipProtocolEndpoint2 = new IPProtocolEndpoint();
		ipProtocolEndpoint2.setIPv4Address("192.168.1.2");
		ipProtocolEndpoint2.setSubnetMask("255.255.255.0");
		vrrProtocolEndpoint2.bindServiceAccessPoint(ipProtocolEndpoint2);

		EthernetPort eth2 = new EthernetPort();
		eth2.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth2.setName("fe-1/3/2");
		ipProtocolEndpoint2.addLogiaclPort(eth2);

		return vrrpGroup;
	}
}
