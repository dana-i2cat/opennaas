/**
 * 
 */
package org.opennaas.web.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.web.entities.Interface;
import org.opennaas.web.entities.LogicalRouter;
import org.opennaas.web.entities.VCPENetwork;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jordi
 */
public class TemplateUtils {

	@Autowired
	private Properties	templateProperties;

	public VCPENetwork getDefaultVCPENetwork() {
		// VCPENetwork
		VCPENetwork vcpeNetwork = new VCPENetwork();
		vcpeNetwork.setClientIpRange(templateProperties.getProperty("vcpenetwork.client.iprange"));
		vcpeNetwork.setTemplate(templateProperties.getProperty("vcpenetwork.template"));

		// Logical Router1
		LogicalRouter logicalRouter1 = new LogicalRouter();
		logicalRouter1.setName(templateProperties.getProperty("vcpenetwork.router1.name"));
		logicalRouter1.setTemplateName(VCPETemplate.VCPE1_ROUTER);

		// Interfaces router1
		List<Interface> interfaces = new ArrayList<Interface>();
		logicalRouter1.setInterfaces(interfaces);
		Interface interface1 = new Interface();
		Interface interface2 = new Interface();
		Interface interface3 = new Interface();
		interfaces.add(interface1);
		interfaces.add(interface2);
		interfaces.add(interface3);

		interface1.setName(templateProperties.getProperty("vcpenetwork.router1.interface1.name"));
		interface1.setPort(templateProperties.getProperty("vcpenetwork.router1.interface1.port"));
		interface1.setVlan(Integer.valueOf(templateProperties.getProperty("vcpenetwork.router1.interface1.vlan").trim()));
		interface1.setIpAddress(templateProperties.getProperty("vcpenetwork.router1.interface1.ipaddress"));
		interface1.setTemplateName(VCPETemplate.INTER1_INTERFACE_LOCAL);
		interface1.setLabelName(Interface.Types.INTER.toString());

		interface2.setName(templateProperties.getProperty("vcpenetwork.router1.interface2.name"));
		interface2.setPort(templateProperties.getProperty("vcpenetwork.router1.interface2.port"));
		interface2.setVlan(Integer.valueOf(templateProperties.getProperty("vcpenetwork.router1.interface2.vlan").trim()));
		interface2.setIpAddress(templateProperties.getProperty("vcpenetwork.router1.interface2.ipaddress"));
		interface2.setTemplateName(VCPETemplate.DOWN1_INTERFACE_LOCAL);
		interface2.setLabelName(Interface.Types.DOWN.toString());

		interface3.setName(templateProperties.getProperty("vcpenetwork.router1.interface3.name"));
		interface3.setPort(templateProperties.getProperty("vcpenetwork.router1.interface3.port"));
		interface3.setVlan(Integer.valueOf(templateProperties.getProperty("vcpenetwork.router1.interface3.vlan").trim()));
		interface3.setIpAddress(templateProperties.getProperty("vcpenetwork.router1.interface3.ipaddress"));
		interface3.setTemplateName(VCPETemplate.UP1_INTERFACE_LOCAL);
		interface3.setLabelName(Interface.Types.UP.toString());

		// Logical Router2
		LogicalRouter logicalRouter2 = new LogicalRouter();
		logicalRouter2.setName(templateProperties.getProperty("vcpenetwork.router2.name"));
		logicalRouter1.setTemplateName(VCPETemplate.VCPE2_ROUTER);

		// Interfaces router2
		interfaces = new ArrayList<Interface>();
		interface1 = new Interface();
		interface2 = new Interface();
		interface3 = new Interface();
		interfaces.add(interface1);
		interfaces.add(interface2);
		interfaces.add(interface3);
		logicalRouter2.setInterfaces(interfaces);

		interface1.setName(templateProperties.getProperty("vcpenetwork.router2.interface1.name"));
		interface1.setPort(templateProperties.getProperty("vcpenetwork.router2.interface1.port"));
		interface1.setVlan(Integer.valueOf(templateProperties.getProperty("vcpenetwork.router2.interface1.vlan").trim()));
		interface1.setIpAddress(templateProperties.getProperty("vcpenetwork.router2.interface1.ipaddress"));
		interface1.setTemplateName(VCPETemplate.INTER2_INTERFACE_LOCAL);
		interface1.setLabelName(Interface.Types.INTER.toString());

		interface2.setName(templateProperties.getProperty("vcpenetwork.router2.interface2.name"));
		interface2.setPort(templateProperties.getProperty("vcpenetwork.router2.interface2.port"));
		interface2.setVlan(Integer.valueOf(templateProperties.getProperty("vcpenetwork.router2.interface2.vlan").trim()));
		interface2.setIpAddress(templateProperties.getProperty("vcpenetwork.router2.interface2.ipaddress"));
		interface2.setTemplateName(VCPETemplate.DOWN2_INTERFACE_LOCAL);
		interface2.setLabelName(Interface.Types.DOWN.toString());

		interface3.setName(templateProperties.getProperty("vcpenetwork.router2.interface3.name"));
		interface3.setPort(templateProperties.getProperty("vcpenetwork.router2.interface3.port"));
		interface3.setVlan(Integer.valueOf(templateProperties.getProperty("vcpenetwork.router2.interface3.vlan").trim()));
		interface3.setIpAddress(templateProperties.getProperty("vcpenetwork.router2.interface3.ipaddress"));
		interface3.setTemplateName(VCPETemplate.UP2_INTERFACE_LOCAL);
		interface3.setLabelName(Interface.Types.UP.toString());

		vcpeNetwork.setLogicalRouter1(logicalRouter1);
		vcpeNetwork.setLogicalRouter2(logicalRouter2);

		return vcpeNetwork;
	}
}
