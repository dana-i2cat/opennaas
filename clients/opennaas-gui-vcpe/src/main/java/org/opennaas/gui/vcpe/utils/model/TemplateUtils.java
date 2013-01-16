/**
 * 
 */
package org.opennaas.gui.vcpe.utils.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.gui.vcpe.entities.BGP;
import org.opennaas.gui.vcpe.entities.BoD;
import org.opennaas.gui.vcpe.entities.Interface;
import org.opennaas.gui.vcpe.entities.LogicalRouter;
import org.opennaas.gui.vcpe.entities.VCPENetwork;
import org.opennaas.gui.vcpe.entities.VRRP;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jordi
 */
public class TemplateUtils {

	@Autowired
	private Properties	templateProperties;

	public VCPENetwork getDefaultVCPENetwork(VCPENetwork vcpeNetwork) {
		// VCPENetwork
		vcpeNetwork.setClientIpRange(templateProperties.getProperty("vcpenetwork.client.iprange"));

		// Logical Router1
		LogicalRouter logicalRouterMaster = vcpeNetwork.getLogicalRouterMaster();
		logicalRouterMaster.setName(templateProperties.getProperty("vcpenetwork.logicalrouter1.name"));
		logicalRouterMaster.setTemplateName(VCPETemplate.VCPE1_ROUTER);

		// Interfaces router1
		Interface ifaceInter = getInterfaceByType(logicalRouterMaster, Interface.Types.INTER.toString());
		Interface ifaceDown = getInterfaceByType(logicalRouterMaster, Interface.Types.DOWN.toString());
		Interface ifaceUp = getInterfaceByType(logicalRouterMaster, Interface.Types.UP.toString());
		Interface ifaceLoopback = getInterfaceByType(logicalRouterMaster, Interface.Types.LOOPBACK.toString());

		ifaceInter.setPort(templateProperties.getProperty("vcpenetwork.logicalrouter1.interface.inter.port"));
		ifaceInter.setVlan(Integer.valueOf(templateProperties.getProperty("vcpenetwork.logicalrouter1.interface.inter.vlan").trim()));
		ifaceInter.setIpAddress(templateProperties.getProperty("vcpenetwork.logicalrouter1.interface.inter.ipaddress"));
		ifaceInter.setTemplateName(VCPETemplate.INTER1_INTERFACE_LOCAL);
		ifaceInter.setType(Interface.Types.INTER.toString());

		ifaceDown.setPort(templateProperties.getProperty("vcpenetwork.logicalrouter1.interface.down.port"));
		ifaceDown.setVlan(Integer.valueOf(templateProperties.getProperty("vcpenetwork.logicalrouter1.interface.down.vlan").trim()));
		ifaceDown.setIpAddress(templateProperties.getProperty("vcpenetwork.logicalrouter1.interface.down.ipaddress"));
		ifaceDown.setTemplateName(VCPETemplate.DOWN1_INTERFACE_LOCAL);
		ifaceDown.setType(Interface.Types.DOWN.toString());

		ifaceUp.setPort(templateProperties.getProperty("vcpenetwork.logicalrouter1.interface.up.port"));
		ifaceUp.setVlan(Integer.valueOf(templateProperties.getProperty("vcpenetwork.logicalrouter1.interface.up.vlan").trim()));
		ifaceUp.setIpAddress(templateProperties.getProperty("vcpenetwork.logicalrouter1.interface.up.ipaddress"));
		ifaceUp.setTemplateName(VCPETemplate.UP1_INTERFACE_LOCAL);
		ifaceUp.setType(Interface.Types.UP.toString());

		// Logical Router2
		LogicalRouter logicalRouterBackup = vcpeNetwork.getLogicalRouterBackup();
		logicalRouterBackup.setName(templateProperties.getProperty("vcpenetwork.logicalrouter2.name"));
		logicalRouterBackup.setTemplateName(VCPETemplate.VCPE2_ROUTER);

		// Interfaces router2
		ifaceInter = getInterfaceByType(logicalRouterBackup, Interface.Types.INTER.toString());
		ifaceDown = getInterfaceByType(logicalRouterBackup, Interface.Types.DOWN.toString());
		ifaceUp = getInterfaceByType(logicalRouterBackup, Interface.Types.UP.toString());
		ifaceLoopback = getInterfaceByType(logicalRouterBackup, Interface.Types.LOOPBACK.toString());

		ifaceInter.setPort(templateProperties.getProperty("vcpenetwork.logicalrouter2.interface.inter.port"));
		ifaceInter.setVlan(Integer.valueOf(templateProperties.getProperty("vcpenetwork.logicalrouter2.interface.inter.vlan").trim()));
		ifaceInter.setIpAddress(templateProperties.getProperty("vcpenetwork.logicalrouter2.interface.inter.ipaddress"));
		ifaceInter.setTemplateName(VCPETemplate.INTER2_INTERFACE_LOCAL);
		ifaceInter.setType(Interface.Types.INTER.toString());

		ifaceDown.setPort(templateProperties.getProperty("vcpenetwork.logicalrouter2.interface.down.port"));
		ifaceDown.setVlan(Integer.valueOf(templateProperties.getProperty("vcpenetwork.logicalrouter2.interface.down.vlan").trim()));
		ifaceDown.setIpAddress(templateProperties.getProperty("vcpenetwork.logicalrouter2.interface.down.ipaddress"));
		ifaceDown.setTemplateName(VCPETemplate.DOWN2_INTERFACE_LOCAL);
		ifaceDown.setType(Interface.Types.DOWN.toString());

		ifaceUp.setPort(templateProperties.getProperty("vcpenetwork.logicalrouter2.interface.up.port"));
		ifaceUp.setVlan(Integer.valueOf(templateProperties.getProperty("vcpenetwork.logicalrouter2.interface.up.vlan").trim()));
		ifaceUp.setIpAddress(templateProperties.getProperty("vcpenetwork.logicalrouter2.interface.up.ipaddress"));
		ifaceUp.setTemplateName(VCPETemplate.UP2_INTERFACE_LOCAL);
		ifaceUp.setType(Interface.Types.UP.toString());

		vcpeNetwork.setLogicalRouterMaster(logicalRouterMaster);
		vcpeNetwork.setLogicalRouterBackup(logicalRouterBackup);

		// BoD
		BoD bod = new BoD();
		Interface ifaceClient1 = new Interface();
		Interface ifaceClient2 = new Interface();

		ifaceClient1.setName(templateProperties.getProperty("vcpenetwork.logicalrouter1.interface.client.name"));
		ifaceClient1.setPort(templateProperties.getProperty("vcpenetwork.logicalrouter1.interface.client.port"));
		ifaceClient1.setVlan(Integer.valueOf(templateProperties.getProperty("vcpenetwork.logicalrouter1.interface.client.vlan").trim()));
		ifaceClient1.setType(Interface.Types.CLIENT.toString());
		ifaceClient1.setTemplateName(VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN);

		ifaceClient2.setName(templateProperties.getProperty("vcpenetwork.logicalrouter2.interface.client.name"));
		ifaceClient2.setPort(templateProperties.getProperty("vcpenetwork.logicalrouter2.interface.client.port"));
		ifaceClient2.setVlan(Integer.valueOf(templateProperties.getProperty("vcpenetwork.logicalrouter2.interface.client.vlan").trim()));
		ifaceClient2.setType(Interface.Types.CLIENT.toString());
		ifaceClient2.setTemplateName(VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN);

		bod.setIfaceClient(ifaceClient1);
		bod.setIfaceClientBackup(ifaceClient2);

		vcpeNetwork.setBod(bod);

		// VRRP
		VRRP vrrp = new VRRP();
		vrrp.setVirtualIPAddress(templateProperties.getProperty("vcpenetwork.vrrp.virtualIPAddress"));
		vcpeNetwork.setVrrp(vrrp);

		// BGP
		BGP bgp = new BGP();
		bgp.setClientASNumber(templateProperties.getProperty("vcpenetwork.bgp.clientASNumber"));
		bgp.setNocASNumber(templateProperties.getProperty("vcpenetwork.bgp.nocASNumber"));
		List<String> clientPrefixes = new ArrayList<String>();
		clientPrefixes.add(templateProperties.getProperty("vcpenetwork.bgp.clientPrefixes"));
		bgp.setClientPrefixes(clientPrefixes);
		vcpeNetwork.setBgp(bgp);

		return vcpeNetwork;
	}

	/**
	 * @param logicalRouterMaster
	 * @param inter
	 * @return
	 */
	private Interface getInterfaceByType(LogicalRouter logicalRouter, String type) {
		List<Interface> interfaces = logicalRouter.getInterfaces();
		Interface iface = null;
		for (int i = 0; i < interfaces.size(); i++) {
			iface = interfaces.get(i).getType().equals(type) ? interfaces.get(i) : iface;
		}
		return iface;
	}
}
