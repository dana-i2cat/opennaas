package org.opennaas.extensions.vcpe.manager.templates.sp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 */
public class VCPETemplateSuggestor {

	private static final String			PROPERTIES_PATH		= "/templates/template.properties";

	private Properties					props;

	/**
	 * Maps TemplateName to properties name.
	 */
	private static Map<String, String>	propertiesNameMap	= new HashMap<String, String>();

	static {

		// Physical elements
		propertiesNameMap.put(VCPETemplate.CORE_PHY_ROUTER, "vcpenetwork.routercore");
		propertiesNameMap.put(VCPETemplate.CORE_PHY_INTERFACE_MASTER, "vcpenetwork.routercore.interface.master");
		propertiesNameMap.put(VCPETemplate.CORE_PHY_INTERFACE_BKP, "vcpenetwork.routercore.interface.bkp");
		propertiesNameMap.put(VCPETemplate.CORE_PHY_LO_INTERFACE, "vcpenetwork.routercore.interface.lo");

		propertiesNameMap.put(VCPETemplate.CPE1_PHY_ROUTER, "vcpenetwork.router1");
		propertiesNameMap.put(VCPETemplate.INTER1_PHY_INTERFACE_LOCAL, "vcpenetwork.router1.interface.inter");
		propertiesNameMap.put(VCPETemplate.DOWN1_PHY_INTERFACE_LOCAL, "vcpenetwork.router1.interface.down");
		propertiesNameMap.put(VCPETemplate.UP1_PHY_INTERFACE_LOCAL, "vcpenetwork.router1.interface.up");
		propertiesNameMap.put(VCPETemplate.LO1_PHY_INTERFACE, "vcpenetwork.router1.interface.lo");

		propertiesNameMap.put(VCPETemplate.CPE2_PHY_ROUTER, "vcpenetwork.router2");
		propertiesNameMap.put(VCPETemplate.INTER2_PHY_INTERFACE_LOCAL, "vcpenetwork.router2.interface.inter");
		propertiesNameMap.put(VCPETemplate.DOWN2_PHY_INTERFACE_LOCAL, "vcpenetwork.router2.interface.down");
		propertiesNameMap.put(VCPETemplate.UP2_PHY_INTERFACE_LOCAL, "vcpenetwork.router2.interface.up");
		propertiesNameMap.put(VCPETemplate.LO2_PHY_INTERFACE, "vcpenetwork.router2.interface.lo");

		propertiesNameMap.put(VCPETemplate.AUTOBAHN, "vcpenetwork.bod");
		propertiesNameMap.put(VCPETemplate.INTER1_PHY_INTERFACE_AUTOBAHN, "vcpenetwork.router1.interface.inter.other");
		propertiesNameMap.put(VCPETemplate.DOWN1_PHY_INTERFACE_AUTOBAHN, "vcpenetwork.router1.interface.down.other");
		propertiesNameMap.put(VCPETemplate.INTER2_PHY_INTERFACE_AUTOBAHN, "vcpenetwork.router2.interface.inter.other");
		propertiesNameMap.put(VCPETemplate.DOWN2_PHY_INTERFACE_AUTOBAHN, "vcpenetwork.router2.interface.down.other");
		propertiesNameMap.put(VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN, "vcpenetwork.router1.interface.client");
		propertiesNameMap.put(VCPETemplate.CLIENT2_PHY_INTERFACE_AUTOBAHN, "vcpenetwork.router2.interface.client");

		// Logical elements
		propertiesNameMap.put(VCPETemplate.UP1_INTERFACE_PEER, "vcpenetwork.logicalrouter1.interface.up.other"); // matching CORE_PHY_INTERFACE_MASTER
		propertiesNameMap.put(VCPETemplate.UP2_INTERFACE_PEER, "vcpenetwork.logicalrouter2.interface.up.other"); // matching CORE_PHY_INTERFACE_BKP
		propertiesNameMap.put(VCPETemplate.CORE_LO_INTERFACE, "vcpenetwork.routercore.interface.lo");

		propertiesNameMap.put(VCPETemplate.VCPE1_ROUTER, "vcpenetwork.logicalrouter1");
		propertiesNameMap.put(VCPETemplate.INTER1_INTERFACE_LOCAL, "vcpenetwork.logicalrouter1.interface.inter");
		propertiesNameMap.put(VCPETemplate.DOWN1_INTERFACE_LOCAL, "vcpenetwork.logicalrouter1.interface.down");
		propertiesNameMap.put(VCPETemplate.UP1_INTERFACE_LOCAL, "vcpenetwork.logicalrouter1.interface.up");
		propertiesNameMap.put(VCPETemplate.LO1_INTERFACE, "vcpenetwork.logicalrouter1.interface.lo");

		propertiesNameMap.put(VCPETemplate.VCPE2_ROUTER, "vcpenetwork.logicalrouter2");
		propertiesNameMap.put(VCPETemplate.INTER2_INTERFACE_LOCAL, "vcpenetwork.logicalrouter2.interface.inter");
		propertiesNameMap.put(VCPETemplate.DOWN2_INTERFACE_LOCAL, "vcpenetwork.logicalrouter2.interface.down");
		propertiesNameMap.put(VCPETemplate.UP2_INTERFACE_LOCAL, "vcpenetwork.logicalrouter2.interface.up");
		propertiesNameMap.put(VCPETemplate.LO2_INTERFACE, "vcpenetwork.logicalrouter2.interface.lo");

		propertiesNameMap.put(VCPETemplate.INTER1_INTERFACE_AUTOBAHN, "vcpenetwork.logicalrouter1.interface.inter.other");
		propertiesNameMap.put(VCPETemplate.DOWN1_INTERFACE_AUTOBAHN, "vcpenetwork.logicalrouter1.interface.down.other");
		propertiesNameMap.put(VCPETemplate.INTER2_INTERFACE_AUTOBAHN, "vcpenetwork.logicalrouter2.interface.inter.other");
		propertiesNameMap.put(VCPETemplate.DOWN2_INTERFACE_AUTOBAHN, "vcpenetwork.logicalrouter2.interface.down.other");
		propertiesNameMap.put(VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN, "vcpenetwork.logicalrouter1.interface.client");
		propertiesNameMap.put(VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN, "vcpenetwork.logicalrouter2.interface.client");

		propertiesNameMap.put("VRRP", "vcpenetwork.vrrp");
		propertiesNameMap.put("BGP", "vcpenetwork.bgp");

	}

	/**
	 * @throws VCPENetworkManagerException
	 * 
	 */
	public VCPETemplateSuggestor() throws VCPENetworkManagerException {
		try {
			props = new Properties();
			props.load(this.getClass().getResourceAsStream(PROPERTIES_PATH));

		} catch (IOException e) {
			throw new VCPENetworkManagerException("Failed to initialize template suggestor");
		}
	}

	/**
	 * 
	 * @param physicalModel
	 *            containing ALL physical elements in the template
	 * @return physicalModel populated with suggested values
	 */
	public VCPENetworkModel getSuggestionForPhysicalModel(VCPENetworkModel physicalModel) {
		// TODO suggested mapping should be more intelligent, not properties driven
		return mapPhysicalElementsFromProperties(physicalModel);
	}

	/**
	 * 
	 * @param logicalModel
	 *            containing ALL logical elements in the template
	 * @return logicalModel populated with a suggested values
	 */
	public VCPENetworkModel getSuggestionForLogicalModel(VCPENetworkModel logicalModel) {
		// TODO suggested mapping should be more intelligent, not properties driven
		return mapLogicalElementsFromProperties(logicalModel);
	}

	private VCPENetworkModel mapPhysicalElementsFromProperties(VCPENetworkModel model) {

		Router core = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CORE_PHY_ROUTER);
		core.setName(props.getProperty(propertiesNameMap.get(VCPETemplate.CORE_PHY_ROUTER) + ".name").trim());
		for (Interface iface : core.getInterfaces()) {
			updatePhysicalInterfaceNameFromProperties(iface);
		}

		// Router1
		Router r1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE1_PHY_ROUTER);
		r1.setName(props.getProperty(propertiesNameMap.get(VCPETemplate.CPE1_PHY_ROUTER) + ".name").trim());
		for (Interface iface : r1.getInterfaces()) {
			updatePhysicalInterfaceNameFromProperties(iface);
		}

		// Router2
		Router r2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE2_PHY_ROUTER);
		r2.setName(props.getProperty(propertiesNameMap.get(VCPETemplate.CPE2_PHY_ROUTER) + ".name").trim());
		for (Interface iface : r2.getInterfaces()) {
			updatePhysicalInterfaceNameFromProperties(iface);
		}

		// BoD
		Domain autobahn = (Domain) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.AUTOBAHN);
		autobahn.setName(props.getProperty(propertiesNameMap.get(VCPETemplate.AUTOBAHN) + ".name").trim());
		for (Interface iface : autobahn.getInterfaces()) {
			updatePhysicalInterfaceNameFromProperties(iface);
		}

		return model;
	}

	private VCPENetworkModel mapLogicalElementsFromProperties(VCPENetworkModel model) {

		// Logical Router 1
		Router vcpe1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		vcpe1.setName(props.getProperty(propertiesNameMap.get(VCPETemplate.VCPE1_ROUTER) + ".name").trim());
		for (Interface iface : vcpe1.getInterfaces()) {
			updateInterfaceFromProperties(iface);
		}

		// Logical Router 2
		Router vcpe2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);
		vcpe2.setName(props.getProperty(propertiesNameMap.get(VCPETemplate.VCPE2_ROUTER) + ".name").trim());
		for (Interface iface : vcpe2.getInterfaces()) {
			updateInterfaceFromProperties(iface);
		}

		// BoD
		List<Interface> bodInterfaces = new ArrayList<Interface>();
		bodInterfaces.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN));
		bodInterfaces.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN));
		bodInterfaces.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER1_INTERFACE_AUTOBAHN));
		bodInterfaces.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER2_INTERFACE_AUTOBAHN));
		bodInterfaces.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN1_INTERFACE_AUTOBAHN));
		bodInterfaces.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN2_INTERFACE_AUTOBAHN));
		for (Interface iface : bodInterfaces) {
			// these interfaces have no ip address,
			// updateInterfaceFromProperties implementation will set it to null, when ipaddress property is not defined
			updateInterfaceFromProperties(iface);
		}

		// Noc network
		Interface up1other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP1_INTERFACE_PEER);
		updateInterfaceFromProperties(up1other);

		Interface up2other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP2_INTERFACE_PEER);
		updateInterfaceFromProperties(up2other);

		Interface lo = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CORE_LO_INTERFACE);
		updateInterfaceFromProperties(lo);

		// VRRP
		int vrrpGoup = Integer.parseInt(props.getProperty("vcpenetwork.vrrp.group").trim());
		int masterVRRPPriority = Integer.parseInt(props.getProperty("vcpenetwork.vrrp.master.priority").trim());
		int backupVRRPPriority = Integer.parseInt(props.getProperty("vcpenetwork.vrrp.backup.priority").trim());
		model.getVrrp().setGroup(vrrpGoup);
		model.getVrrp().setPriorityMaster(masterVRRPPriority);
		model.getVrrp().setPriorityBackup(backupVRRPPriority);
		model.getVrrp().setVirtualIPAddress(props.getProperty("vcpenetwork.vrrp.virtualIPAddress").trim());

		// BGP
		model.getBgp().setClientASNumber(props.getProperty("vcpenetwork.bgp.clientASNumber").trim());
		model.getBgp().setNocASNumber(props.getProperty("vcpenetwork.bgp.nocASNumber").trim());
		List<String> clientPrefixes = new ArrayList<String>();
		clientPrefixes.add(props.getProperty("vcpenetwork.bgp.clientPrefixes").trim());
		model.getBgp().setCustomerPrefixes(clientPrefixes);

		// VCPE
		model.setClientIpRange(props.getProperty("vcpenetwork.client.iprange").trim());

		return model;
	}

	private Interface updateInterfaceFromProperties(Interface iface) {

		String propertiesName = propertiesNameMap.get(iface.getTemplateName());

		long vlan = 0L;
		if (props.getProperty(propertiesName + ".vlan") != null)
			vlan = Long.parseLong(props.getProperty(propertiesName + ".vlan").trim());

		int port = 0;
		if (props.getProperty(propertiesName + ".port") != null)
			port = Integer.parseInt(props.getProperty(propertiesName + ".port").trim());

		String ipAddress = null;
		if (props.getProperty(propertiesName + ".ipaddress") != null)
			ipAddress = props.getProperty(propertiesName + ".ipaddress").trim();

		return VCPENetworkModelHelper.updateInterface(iface,
				props.getProperty(propertiesName + ".name").trim() + "." + port,
				vlan,
				ipAddress,
				props.getProperty(propertiesName + ".name").trim(),
				port);
	}

	private Interface updatePhysicalInterfaceNameFromProperties(Interface iface) {
		String propertiesName = propertiesNameMap.get(iface.getTemplateName());
		iface.setPhysicalInterfaceName(props.getProperty(propertiesName + ".name").trim());
		iface.setName(iface.getPhysicalInterfaceName());

		return iface;

	}
}
