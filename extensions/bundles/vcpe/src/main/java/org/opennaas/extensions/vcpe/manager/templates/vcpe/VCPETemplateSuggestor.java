package org.opennaas.extensions.vcpe.manager.templates.vcpe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.LongRange;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.manager.IVCPENetworkManager;
import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
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

	/**
	 * Maps TemplateName to properties name.
	 */
	private static Map<String, String>	propertiesNameMap	= new HashMap<String, String>();

	private Properties					props;

	private IVCPENetworkManager			vcpeManager;

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

			vcpeManager = loadVCPEManager();

		} catch (IOException e) {
			throw new VCPENetworkManagerException("Failed to initialize template suggestor." + e.getMessage());
		} catch (ActivatorException e) {
			throw new VCPENetworkManagerException("Failed to initialize template suggestor." + e.getMessage());
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
		VCPENetworkModel updated = mapLogicalElementsFromProperties(logicalModel);
		updated = suggestVLANs(updated);
		updated = suggestUnits(updated);
		return updated;

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

	/**
	 * Suggest and assign vlans for logical interfaces in given model.
	 * 
	 * @param model
	 * @return
	 */
	private VCPENetworkModel suggestVLANs(VCPENetworkModel model) {

		// TODO It may happen that each link has different vlan ranges.
		LongRange vlanRange = getVLANRange();

		// Logical Router 1
		Router vcpe1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		for (Interface iface : vcpe1.getInterfaces()) {
			if (!iface.getTemplateName().equals(VCPETemplate.LO1_INTERFACE))
				iface.setVlan(suggestVLAN(vcpe1, iface, vlanRange));
		}

		// Logical Router 2
		Router vcpe2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);
		for (Interface iface : vcpe2.getInterfaces()) {
			if (!iface.getTemplateName().equals(VCPETemplate.LO2_INTERFACE))
				iface.setVlan(suggestVLAN(vcpe2, iface, vlanRange));
		}

		// BoD
		updateIfaceVLANFromLink((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER1_INTERFACE_AUTOBAHN),
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER1_LINK_LOCAL));
		updateIfaceVLANFromLink((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER2_INTERFACE_AUTOBAHN),
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER2_LINK_LOCAL));
		updateIfaceVLANFromLink((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN1_INTERFACE_AUTOBAHN),
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN1_LINK_LOCAL));
		updateIfaceVLANFromLink((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN2_INTERFACE_AUTOBAHN),
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN2_LINK_LOCAL));
		// Should not suggest vlan for client interfaces.
		// Normally each client will have an assigned vlan (or set of vlans) and the NOC should select between the assigned ones.

		// NOC network
		updateIfaceVLANFromLink((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP1_INTERFACE_PEER),
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP1_LINK));
		updateIfaceVLANFromLink((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP2_INTERFACE_PEER),
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP2_LINK));

		return model;
	}

	private VCPENetworkModel suggestUnits(VCPENetworkModel model) {

		// Suggestion strategy:
		// Try to assign the same unit as vlan (if it is free)
		// In interfaces without vlan, or with unit matching vlan already used, assign first unit that isFree.

		IntRange unitRange = new IntRange(1, 4094);

		// Logical Router 1
		Router vcpe1 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		for (Interface iface : vcpe1.getInterfaces()) {
			if (iface.getTemplateName().equals(VCPETemplate.LO1_INTERFACE))
				iface.setPort(suggestInterfaceUnit(vcpe1, iface, unitRange));
			else
				iface.setPort(suggestInterfaceUnit(vcpe1, iface, unitRange, Long.valueOf(iface.getVlan()).intValue()));
		}

		// Logical Router 2
		Router vcpe2 = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);
		for (Interface iface : vcpe2.getInterfaces()) {
			if (iface.getTemplateName().equals(VCPETemplate.LO2_INTERFACE))
				iface.setPort(suggestInterfaceUnit(vcpe2, iface, unitRange));
			else
				iface.setPort(suggestInterfaceUnit(vcpe2, iface, unitRange, Long.valueOf(iface.getVlan()).intValue()));
		}

		// BoD
		Interface bodIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER1_INTERFACE_AUTOBAHN);
		bodIface.setPort(suggestInterfaceUnit(vcpe2, bodIface, unitRange, Long.valueOf(bodIface.getVlan()).intValue()));

		bodIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.INTER2_INTERFACE_AUTOBAHN);
		bodIface.setPort(suggestInterfaceUnit(vcpe2, bodIface, unitRange, Long.valueOf(bodIface.getVlan()).intValue()));

		bodIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN1_INTERFACE_AUTOBAHN);
		bodIface.setPort(suggestInterfaceUnit(vcpe2, bodIface, unitRange, Long.valueOf(bodIface.getVlan()).intValue()));

		bodIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.DOWN2_INTERFACE_AUTOBAHN);
		bodIface.setPort(suggestInterfaceUnit(vcpe2, bodIface, unitRange, Long.valueOf(bodIface.getVlan()).intValue()));

		// NOC network
		Interface upIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP1_INTERFACE_PEER);
		upIface.setPort(suggestInterfaceUnit(vcpe2, upIface, unitRange, Long.valueOf(upIface.getVlan()).intValue()));

		upIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.UP2_INTERFACE_PEER);
		upIface.setPort(suggestInterfaceUnit(vcpe2, upIface, unitRange, Long.valueOf(upIface.getVlan()).intValue()));

		return model;
	}

	/**
	 * 
	 * @param router
	 * @param iface
	 * @param vlanRange
	 * @return first vlan in vlanRange that is free in given interface of given router.
	 */
	private long suggestVLAN(Router router, Interface iface, LongRange vlanRange) {
		for (long vlan : vlanRange.toArray()) {
			if (getVCPENetworkManager().isVLANFree(null, Long.toString(vlan), iface.getPhysicalInterfaceName())) {
				return vlan;
			}
		}
		return 0L;
	}

	/**
	 * 
	 * @param router
	 * @param iface
	 * @param unitRange
	 * @param desired
	 *            preferred unit number. This will be the returned value in case it is free.
	 * @return desired unit if it is free in given interface of given router. Otherwise, first unit in unitRange that is free in given interface of
	 *         given router.
	 */
	private int suggestInterfaceUnit(Router router, Interface iface, IntRange unitRange, int desired) {

		if (getVCPENetworkManager().isInterfaceFree(null, iface.getPhysicalInterfaceName() + "." + desired))
			return desired;

		return suggestInterfaceUnit(router, iface, unitRange);
	}

	/**
	 * 
	 * @param router
	 * @param iface
	 * @param unitRange
	 * @return first unit in unitRange that is free in given interface of given router.
	 */
	private int suggestInterfaceUnit(Router router, Interface iface, IntRange unitRange) {
		for (int unitNum : unitRange.toArray()) {
			if (getVCPENetworkManager().isInterfaceFree(null, iface.getPhysicalInterfaceName() + "." + unitNum)) {
				return unitNum;
			}
		}
		return 0;
	}

	/**
	 * Sets iface vlan according to the other endpoint of given link.
	 * 
	 * @param iface
	 * @param link
	 */
	private void updateIfaceVLANFromLink(Interface iface, Link link) {
		if (link.getSource().equals(iface))
			iface.setVlan(link.getSink().getVlan());
		else
			iface.setVlan(link.getSource().getVlan());
	}

	private LongRange getVLANRange() {
		// TODO read from config file
		return new LongRange(1, 4094);
	}

	private IVCPENetworkManager getVCPENetworkManager() {
		return vcpeManager;
	}

	private IVCPENetworkManager loadVCPEManager() throws ActivatorException {
		return Activator.getVCPEManagerService();
	}

}
