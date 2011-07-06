package interfaces;
import java.util.HashMap;

import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.VLANEndpoint;
import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;
import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolManager;
import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolSessionManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

public class ActionTestHelper {

	String	resourceId	= "RandomDevice";

	public ProtocolSessionManager getProtocolSessionManager() {

		ProtocolManager protocolManager = new ProtocolManager();
		ProtocolSessionManager protocolSessionManager = null;
		try {
			protocolSessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceId);
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
		// ADDED
		return protocolSessionContext;

	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	public Object newParamsInterfaceEthernet() {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setElementName("fe-0/3/2");
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		return eth;
	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	public Object newParamsInterfaceLT() {
		LogicalTunnelPort ltp = new LogicalTunnelPort();
		ltp.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		ltp.setElementName("lt-0/3/2");
		ltp.setPeer_unit(101);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		ltp.addProtocolEndpoint(ip);

		return ltp;
	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	public Object newParamsInterfaceLT_VLAN() {
		LogicalTunnelPort ltp = new LogicalTunnelPort();
		ltp.setLinkTechnology(NetworkPort.LinkTechnology.OTHER);
		ltp.setElementName("lt-0/3/2");
		ltp.setPeer_unit(101);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		ltp.addProtocolEndpoint(ip);
		VLANEndpoint vlan = new VLANEndpoint();
		vlan.setVlanID(2);
		ltp.addProtocolEndpoint(vlan);
		return ltp;
	}

	public Object newParamsInterfaceEtherVLAN() {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.OTHER);
		eth.setElementName("fe-0/3/2");
		eth.setPortNumber(2);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		VLANEndpoint vlan = new VLANEndpoint();
		vlan.setVlanID(2);
		eth.addProtocolEndpoint(vlan);
		return eth;
	}
}
