package org.opennaas.extensions.vcpe.manager.templates.sp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.isfree.IsFreeChecker;
import org.opennaas.extensions.vcpe.manager.templates.common.SuggestedValues;
import org.opennaas.extensions.vcpe.manager.templates.common.UnitSuggestor;
import org.opennaas.extensions.vcpe.manager.templates.common.VLANSuggestor;
import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.LogicalRouter;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 */
public class SPTemplateSuggestor {

	private static final String			SUGGESTOR_CONFIGURATION_ID	= "org.opennaas.extensions.vcpe.manager.templates.sp.suggestor";

	// TODO read from config file
	// TODO It may happen that each link has different vlan ranges.
	private static final long			MIN_VLAN					= 1L;
	private static final long			MAX_VLAN					= 4094L;

	/**
	 * Maps TemplateName to properties name.
	 */
	private static Map<String, String>	propertiesNameMap			= new HashMap<String, String>();

	private Properties					props;

	static {

		// Physical elements
		propertiesNameMap.put(SPTemplateConstants.CORE_PHY_ROUTER, "vcpenetwork.routercore");
		propertiesNameMap.put(SPTemplateConstants.CORE_PHY_INTERFACE_MASTER, "vcpenetwork.routercore.interface.master");
		propertiesNameMap.put(SPTemplateConstants.CORE_PHY_INTERFACE_BKP, "vcpenetwork.routercore.interface.bkp");
		propertiesNameMap.put(SPTemplateConstants.CORE_PHY_LO_INTERFACE, "vcpenetwork.routercore.interface.lo");

		propertiesNameMap.put(SPTemplateConstants.CPE1_PHY_ROUTER, "vcpenetwork.router1");
		propertiesNameMap.put(SPTemplateConstants.INTER1_PHY_INTERFACE_LOCAL, "vcpenetwork.router1.interface.inter");
		propertiesNameMap.put(SPTemplateConstants.DOWN1_PHY_INTERFACE_LOCAL, "vcpenetwork.router1.interface.down");
		propertiesNameMap.put(SPTemplateConstants.UP1_PHY_INTERFACE_LOCAL, "vcpenetwork.router1.interface.up");
		propertiesNameMap.put(SPTemplateConstants.LO1_PHY_INTERFACE, "vcpenetwork.router1.interface.lo");

		propertiesNameMap.put(SPTemplateConstants.CPE2_PHY_ROUTER, "vcpenetwork.router2");
		propertiesNameMap.put(SPTemplateConstants.INTER2_PHY_INTERFACE_LOCAL, "vcpenetwork.router2.interface.inter");
		propertiesNameMap.put(SPTemplateConstants.DOWN2_PHY_INTERFACE_LOCAL, "vcpenetwork.router2.interface.down");
		propertiesNameMap.put(SPTemplateConstants.UP2_PHY_INTERFACE_LOCAL, "vcpenetwork.router2.interface.up");
		propertiesNameMap.put(SPTemplateConstants.LO2_PHY_INTERFACE, "vcpenetwork.router2.interface.lo");

		propertiesNameMap.put(SPTemplateConstants.AUTOBAHN, "vcpenetwork.bod");
		propertiesNameMap.put(SPTemplateConstants.INTER1_PHY_INTERFACE_AUTOBAHN, "vcpenetwork.router1.interface.inter.other");
		propertiesNameMap.put(SPTemplateConstants.DOWN1_PHY_INTERFACE_AUTOBAHN, "vcpenetwork.router1.interface.down.other");
		propertiesNameMap.put(SPTemplateConstants.INTER2_PHY_INTERFACE_AUTOBAHN, "vcpenetwork.router2.interface.inter.other");
		propertiesNameMap.put(SPTemplateConstants.DOWN2_PHY_INTERFACE_AUTOBAHN, "vcpenetwork.router2.interface.down.other");
		propertiesNameMap.put(SPTemplateConstants.CLIENT1_PHY_INTERFACE_AUTOBAHN, "vcpenetwork.router1.interface.client");
		propertiesNameMap.put(SPTemplateConstants.CLIENT2_PHY_INTERFACE_AUTOBAHN, "vcpenetwork.router2.interface.client");

		// Logical elements
		propertiesNameMap.put(SPTemplateConstants.UP1_INTERFACE_PEER, "vcpenetwork.logicalrouter1.interface.up.other"); // matching
																														// CORE_PHY_INTERFACE_MASTER
		propertiesNameMap.put(SPTemplateConstants.UP2_INTERFACE_PEER, "vcpenetwork.logicalrouter2.interface.up.other"); // matching
																														// CORE_PHY_INTERFACE_BKP
		propertiesNameMap.put(SPTemplateConstants.CORE_LO_INTERFACE, "vcpenetwork.routercore.interface.lo");

		propertiesNameMap.put(SPTemplateConstants.VCPE1_ROUTER, "vcpenetwork.logicalrouter1");
		propertiesNameMap.put(SPTemplateConstants.INTER1_INTERFACE_LOCAL, "vcpenetwork.logicalrouter1.interface.inter");
		propertiesNameMap.put(SPTemplateConstants.DOWN1_INTERFACE_LOCAL, "vcpenetwork.logicalrouter1.interface.down");
		propertiesNameMap.put(SPTemplateConstants.UP1_INTERFACE_LOCAL, "vcpenetwork.logicalrouter1.interface.up");
		propertiesNameMap.put(SPTemplateConstants.LO1_INTERFACE, "vcpenetwork.logicalrouter1.interface.lo");

		propertiesNameMap.put(SPTemplateConstants.VCPE2_ROUTER, "vcpenetwork.logicalrouter2");
		propertiesNameMap.put(SPTemplateConstants.INTER2_INTERFACE_LOCAL, "vcpenetwork.logicalrouter2.interface.inter");
		propertiesNameMap.put(SPTemplateConstants.DOWN2_INTERFACE_LOCAL, "vcpenetwork.logicalrouter2.interface.down");
		propertiesNameMap.put(SPTemplateConstants.UP2_INTERFACE_LOCAL, "vcpenetwork.logicalrouter2.interface.up");
		propertiesNameMap.put(SPTemplateConstants.LO2_INTERFACE, "vcpenetwork.logicalrouter2.interface.lo");

		propertiesNameMap.put(SPTemplateConstants.INTER1_INTERFACE_AUTOBAHN, "vcpenetwork.logicalrouter1.interface.inter.other");
		propertiesNameMap.put(SPTemplateConstants.DOWN1_INTERFACE_AUTOBAHN, "vcpenetwork.logicalrouter1.interface.down.other");
		propertiesNameMap.put(SPTemplateConstants.INTER2_INTERFACE_AUTOBAHN, "vcpenetwork.logicalrouter2.interface.inter.other");
		propertiesNameMap.put(SPTemplateConstants.DOWN2_INTERFACE_AUTOBAHN, "vcpenetwork.logicalrouter2.interface.down.other");
		propertiesNameMap.put(SPTemplateConstants.CLIENT1_INTERFACE_AUTOBAHN, "vcpenetwork.logicalrouter1.interface.client");
		propertiesNameMap.put(SPTemplateConstants.CLIENT2_INTERFACE_AUTOBAHN, "vcpenetwork.logicalrouter2.interface.client");

		propertiesNameMap.put("VRRP", "vcpenetwork.vrrp");
		propertiesNameMap.put("BGP", "vcpenetwork.bgp");

	}

	/**
	 * @throws VCPENetworkManagerException
	 * 
	 */
	public SPTemplateSuggestor() throws VCPENetworkManagerException {
		try {
			props = ConfigurationAdminUtil.getProperties(Activator.getContext(), SUGGESTOR_CONFIGURATION_ID);
		} catch (IOException e) {
			throw new VCPENetworkManagerException("Failed to initialize template suggestor." + e.getMessage());
		}
		if (props == null)
			throw new VCPENetworkManagerException("Failed to initialize template suggestor." + "Unable to obtain configuration " +
					SUGGESTOR_CONFIGURATION_ID);
	}

	/**
	 * 
	 * @param physicalModel
	 *            containing ALL physical elements in the template
	 * @return physicalModel populated with suggested values
	 * @throws VCPENetworkManagerException
	 *             if failed to suggest a valid vcpe physical model
	 */
	public VCPENetworkModel getSuggestionForPhysicalModel(VCPENetworkModel physicalModel) throws VCPENetworkManagerException {
		// TODO suggested mapping should be more intelligent, not properties driven
		return mapPhysicalElementsFromProperties(physicalModel);
	}

	/**
	 * 
	 * @param logicalModel
	 *            containing ALL logical and physical elements in the template
	 * @return logicalModel populated with a suggested values
	 * @throws VCPENetworkManagerException
	 *             if failed to suggest a valid vcpe logical model
	 */
	public VCPENetworkModel getSuggestionForLogicalModel(VCPENetworkModel logicalModel) throws VCPENetworkManagerException {
		// TODO suggested mapping should be more intelligent, not properties driven
		VCPENetworkModel updated = mapLogicalElementsFromProperties(logicalModel);
		updated = suggestVLANs(updated);
		updated = suggestUnits(updated); // reads suggested VLANS
		updated = suggestVRRP(updated); // reads suggested VLANS
		return updated;

	}

	private VCPENetworkModel mapPhysicalElementsFromProperties(VCPENetworkModel model) {

		Router core = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CORE_PHY_ROUTER);
		core.setName(props.getProperty(propertiesNameMap.get(SPTemplateConstants.CORE_PHY_ROUTER) + ".name").trim());
		for (Interface iface : core.getInterfaces()) {
			updatePhysicalInterfaceNameFromProperties(iface);
		}

		// Router1
		Router r1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CPE1_PHY_ROUTER);
		r1.setName(props.getProperty(propertiesNameMap.get(SPTemplateConstants.CPE1_PHY_ROUTER) + ".name").trim());
		for (Interface iface : r1.getInterfaces()) {
			updatePhysicalInterfaceNameFromProperties(iface);
		}

		// Router2
		Router r2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CPE2_PHY_ROUTER);
		r2.setName(props.getProperty(propertiesNameMap.get(SPTemplateConstants.CPE2_PHY_ROUTER) + ".name").trim());
		for (Interface iface : r2.getInterfaces()) {
			updatePhysicalInterfaceNameFromProperties(iface);
		}

		// BoD
		Domain autobahn = (Domain) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.AUTOBAHN);
		autobahn.setName(props.getProperty(propertiesNameMap.get(SPTemplateConstants.AUTOBAHN) + ".name").trim());
		for (Interface iface : autobahn.getInterfaces()) {
			updatePhysicalInterfaceNameFromProperties(iface);
		}

		return model;
	}

	private VCPENetworkModel mapLogicalElementsFromProperties(VCPENetworkModel model) {

		// Logical Router 1
		Router vcpe1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.VCPE1_ROUTER);
		vcpe1.setName(props.getProperty(propertiesNameMap.get(SPTemplateConstants.VCPE1_ROUTER) + ".name").trim());
		for (Interface iface : vcpe1.getInterfaces()) {
			updateInterfaceFromProperties(iface);
		}

		// Logical Router 2
		Router vcpe2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.VCPE2_ROUTER);
		vcpe2.setName(props.getProperty(propertiesNameMap.get(SPTemplateConstants.VCPE2_ROUTER) + ".name").trim());
		for (Interface iface : vcpe2.getInterfaces()) {
			updateInterfaceFromProperties(iface);
		}

		// BoD
		List<Interface> bodInterfaces = new ArrayList<Interface>();
		bodInterfaces.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CLIENT1_INTERFACE_AUTOBAHN));
		bodInterfaces.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CLIENT2_INTERFACE_AUTOBAHN));
		bodInterfaces.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER1_INTERFACE_AUTOBAHN));
		bodInterfaces.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER2_INTERFACE_AUTOBAHN));
		bodInterfaces.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_INTERFACE_AUTOBAHN));
		bodInterfaces.add((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_INTERFACE_AUTOBAHN));
		for (Interface iface : bodInterfaces) {
			// these interfaces have no ip address,
			// updateInterfaceFromProperties implementation will set it to null, when ipaddress property is not defined
			updateInterfaceFromProperties(iface);
		}

		// Noc network
		Interface up1other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP1_INTERFACE_PEER);
		updateInterfaceFromProperties(up1other);

		Interface up2other = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP2_INTERFACE_PEER);
		updateInterfaceFromProperties(up2other);

		Interface lo = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CORE_LO_INTERFACE);
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
		model.setNocIpRange(props.getProperty("vcpenetwork.noc.iprange").trim());

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

	/**
	 * Suggest and assign vlans for logical interfaces in given model.
	 * 
	 * @param model
	 * @return
	 */
	private VCPENetworkModel suggestVLANs(VCPENetworkModel model) throws VCPENetworkManagerException {

		SuggestedValues suggestedVLANS = new SuggestedValues();

		// Logical Router 1
		LogicalRouter vcpe1 = (LogicalRouter) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.VCPE1_ROUTER);
		for (Interface iface : vcpe1.getInterfaces()) {
			if (!iface.getTemplateName().equals(SPTemplateConstants.LO1_INTERFACE))
				iface.setVlan(VLANSuggestor.suggestVLAN(vcpe1.getPhysicalRouter(), iface, suggestedVLANS));
		}

		// Logical Router 2
		LogicalRouter vcpe2 = (LogicalRouter) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.VCPE2_ROUTER);
		for (Interface iface : vcpe2.getInterfaces()) {
			if (!iface.getTemplateName().equals(SPTemplateConstants.LO2_INTERFACE))
				iface.setVlan(VLANSuggestor.suggestVLAN(vcpe2.getPhysicalRouter(), iface, suggestedVLANS));
		}

		// BoD
		Domain bod = (Domain) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.AUTOBAHN);
		updateIfaceVLANFromLink(bod,
				(Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER1_INTERFACE_AUTOBAHN),
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER1_LINK_LOCAL), suggestedVLANS);
		updateIfaceVLANFromLink(bod,
				(Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER2_INTERFACE_AUTOBAHN),
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER2_LINK_LOCAL), suggestedVLANS);
		updateIfaceVLANFromLink(bod,
				(Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_INTERFACE_AUTOBAHN),
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_LINK_LOCAL), suggestedVLANS);
		updateIfaceVLANFromLink(bod,
				(Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_INTERFACE_AUTOBAHN),
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_LINK_LOCAL), suggestedVLANS);
		// Should not suggest vlan for client interfaces.
		// Normally each client will have an assigned vlan (or set of vlans) and the NOC should select between the assigned ones.

		// NOC network
		Router core = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CORE_PHY_ROUTER);
		updateIfaceVLANFromLink(core, (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP1_INTERFACE_PEER),
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP1_LINK), suggestedVLANS);
		updateIfaceVLANFromLink(core, (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP2_INTERFACE_PEER),
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP2_LINK), suggestedVLANS);

		return model;
	}

	private VCPENetworkModel suggestUnits(VCPENetworkModel model) {

		// Suggestion strategy:
		// Try to assign the same unit as vlan (if it is free)
		// In interfaces without vlan, or with unit matching vlan already used, assign first unit that isFree.

		/*
		 * Key: physical interface name. Value: unit assigned to key.
		 */
		SuggestedValues suggestedUnits = new SuggestedValues();

		// Logical Router 1
		LogicalRouter vcpe1 = (LogicalRouter) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.VCPE1_ROUTER);
		for (Interface iface : vcpe1.getInterfaces()) {
			if (iface.getTemplateName().equals(SPTemplateConstants.LO1_INTERFACE))
				iface.setPort(UnitSuggestor.suggestUnit(vcpe1.getPhysicalRouter(), iface, suggestedUnits));
			else
				iface.setPort(UnitSuggestor.suggestUnitFromVLAN(vcpe1.getPhysicalRouter(), iface, suggestedUnits));
		}

		// Logical Router 2
		LogicalRouter vcpe2 = (LogicalRouter) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.VCPE2_ROUTER);
		for (Interface iface : vcpe2.getInterfaces()) {
			if (iface.getTemplateName().equals(SPTemplateConstants.LO2_INTERFACE))
				iface.setPort(UnitSuggestor.suggestUnit(vcpe2.getPhysicalRouter(), iface, suggestedUnits));
			else
				iface.setPort(UnitSuggestor.suggestUnitFromVLAN(vcpe2.getPhysicalRouter(), iface, suggestedUnits));
		}

		// BoD
		Domain bod = (Domain) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.AUTOBAHN);
		Interface bodIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER1_INTERFACE_AUTOBAHN);
		bodIface.setPort(UnitSuggestor.suggestUnitFromVLAN(bod, bodIface, suggestedUnits));

		bodIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER2_INTERFACE_AUTOBAHN);
		bodIface.setPort(UnitSuggestor.suggestUnitFromVLAN(bod, bodIface, suggestedUnits));

		bodIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_INTERFACE_AUTOBAHN);
		bodIface.setPort(UnitSuggestor.suggestUnitFromVLAN(bod, bodIface, suggestedUnits));

		bodIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_INTERFACE_AUTOBAHN);
		bodIface.setPort(UnitSuggestor.suggestUnitFromVLAN(bod, bodIface, suggestedUnits));

		// NOC network
		Router core = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CORE_PHY_ROUTER);
		Interface upIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP1_INTERFACE_PEER);
		upIface.setPort(UnitSuggestor.suggestUnitFromVLAN(core, upIface, suggestedUnits));

		upIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP2_INTERFACE_PEER);
		upIface.setPort(UnitSuggestor.suggestUnitFromVLAN(core, upIface, suggestedUnits));

		return model;
	}

	/**
	 * Sets iface vlan according to the other endpoint of given link.
	 * 
	 * @param phyElement
	 * @param iface
	 * @param link
	 * @param suggestedVLANS
	 */
	private void updateIfaceVLANFromLink(VCPENetworkElement phyElement, Interface iface, Link link, SuggestedValues suggestedVLANs) {
		long vlan = VCPENetworkModelHelper.updateIfaceVLANFromLink(iface, link);

		suggestedVLANs.markAsSuggested(VCPENetworkModelHelper.generatePhysicalInterfaceKey(phyElement, iface), Long.valueOf(vlan).intValue());
	}

	/**
	 * 
	 * @param model
	 * @return given model with suggested vrrp values.
	 * @throws VCPENetworkManagerException
	 *             if failed to suggest a valid vrrp configuration.
	 */
	private VCPENetworkModel suggestVRRP(VCPENetworkModel model) throws VCPENetworkManagerException {
		// priority parameters are read from config file (properties)
		// ip addresses are read from properties (although this may change in the future)

		model.getVrrp().setGroup(suggestVRRPGroup(model));
		if (model.getVrrp().getGroup() == null)
			throw new VCPENetworkManagerException("Fail to suggest valid VRRP configuration. Unable to find an available VRRP group");
		return model;
	}

	/**
	 * 
	 * @param model
	 * @return suggested VCPEGroup, or null if there is no available vrrpGroup.
	 */
	private Integer suggestVRRPGroup(VCPENetworkModel model) {
		boolean found = false;
		// There is commonly one vrrpGroup per vlan (as there is one gw per LAN)
		// VRRPGroup is commonly assigned the vlan tag
		// So here, for VRRPGroup, we use vlan tag of the client LAN. That is, vlan of down interfaces
		// (using master one, as down vlans may differ between master and backup)
		Integer vrrpGroup = null;
		long vlanTag = ((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_INTERFACE_LOCAL)).getVlan();
		if (IsFreeChecker.isVRRPGroupFree(model.getId(), String.valueOf(vlanTag))) {
			vrrpGroup = Long.valueOf(vlanTag).intValue();
			found = true;
		}

		for (int i = 1; i < MAX_VLAN && !found; i++) {
			if (IsFreeChecker.isVRRPGroupFree(model.getId(), String.valueOf(i))) {
				vrrpGroup = i;
				found = true;
			}
		}
		return vrrpGroup;
	}
}
