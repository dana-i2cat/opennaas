package org.opennaas.extensions.vcpe.manager.templates.mp;

import java.io.IOException;

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.templates.common.SuggestedValues;
import org.opennaas.extensions.vcpe.manager.templates.common.UnitSuggestor;
import org.opennaas.extensions.vcpe.manager.templates.common.VLANSuggestor;
import org.opennaas.extensions.vcpe.model.IPNetworkDomain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class MPTemplateSuggestor {

	private MPTemplateDefaultValuesLoader	defaultsLoader;

	public MPTemplateSuggestor() {
		defaultsLoader = new MPTemplateDefaultValuesLoader();
	}

	public void initialize() throws VCPENetworkManagerException {
		try {
			defaultsLoader.initialize();
		} catch (IOException e) {
			throw new VCPENetworkManagerException(e);
		}
	}

	/**
	 * 
	 * @param physicalModel
	 *            containing ALL physical elements in the template, and nothing else
	 * @return physicalModel populated with suggested values
	 * @throws VCPENetworkManagerException
	 *             if failed to suggest a valid vcpe physical model
	 */
	public VCPENetworkModel getSuggestionForPhysicalModel(VCPENetworkModel physicalModel) throws VCPENetworkManagerException {
		if (!defaultsLoader.isInitialized())
			throw new VCPENetworkManagerException("Suggestor is not initialized");

		// suggestion made from default values
		return defaultsLoader.loadDefaultPhysicalModel(physicalModel);
	}

	/**
	 * Populates given logicalModel with suggested values. Given physicalModel is used as read-only: It's used for suggesting, but it's not modified
	 * within this method.
	 * 
	 * @param physicalModel
	 *            containing ALL physical elements in the template, and nothing else
	 * @param logicalModel
	 *            containing ALL logical elements in the template, and nothing else
	 * @return given logicalModel with logical elements populated with suggested values, no elements are added to logicalModel within this method.
	 * @throws VCPENetworkManagerException
	 *             if failed to suggest a valid vcpe logical model
	 */
	public VCPENetworkModel getSuggestionForLogicalModel(VCPENetworkModel physicalModel, VCPENetworkModel logicalModel)
			throws VCPENetworkManagerException {
		if (!defaultsLoader.isInitialized())
			throw new VCPENetworkManagerException("Suggestor is not initialized");

		// suggestion made from default values
		logicalModel = defaultsLoader.loadDefaultLogicalModel(logicalModel);
		logicalModel = suggestVLANs(physicalModel, logicalModel);
		logicalModel = suggestUnits(physicalModel, logicalModel);

		return logicalModel;
	}

	private VCPENetworkModel suggestVLANs(VCPENetworkModel physicalModel, VCPENetworkModel logicalModel) {

		Integer vlan;
		VCPENetworkElement phyElement;
		Interface phyIface;
		Interface target;

		SuggestedValues suggestedVLANs = new SuggestedValues();

		// suggest VLANs for router interfaces
		phyElement = (Router) VCPENetworkModelHelper.getElementByTemplateName(physicalModel, TemplateConstants.ROUTER_1_PHY);
		phyIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(physicalModel, TemplateConstants.ROUTER_1_PHY_IFACE_UP1);
		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LR_1_IFACE_UP);
		vlan = VLANSuggestor.suggestVLANWithPreference(phyElement, phyIface, suggestedVLANs, Long.valueOf(target.getVlan()).intValue());
		target.setVlan(vlan);

		phyElement = (Router) VCPENetworkModelHelper.getElementByTemplateName(physicalModel, TemplateConstants.ROUTER_1_PHY);
		phyIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(physicalModel, TemplateConstants.ROUTER_1_PHY_IFACE_UP2);
		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LR_2_IFACE_UP);
		vlan = VLANSuggestor.suggestVLANWithPreference(phyElement, phyIface, suggestedVLANs, Long.valueOf(target.getVlan()).intValue());
		target.setVlan(vlan);

		phyElement = (Router) VCPENetworkModelHelper.getElementByTemplateName(physicalModel, TemplateConstants.ROUTER_1_PHY);
		phyIface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(physicalModel, TemplateConstants.ROUTER_1_PHY_IFACE_DOWN);
		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LR_CLIENT_IFACE_DOWN);
		vlan = VLANSuggestor.suggestVLANWithPreference(phyElement, phyIface, suggestedVLANs, Long.valueOf(target.getVlan()).intValue());
		target.setVlan(vlan);

		// update other endpoints of links with same vlans
		Link link;
		IPNetworkDomain net;

		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.WAN1_IFACE_DOWN);
		link = (Link) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LINK_WAN1);
		net = (IPNetworkDomain) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.WAN1);
		// Passing a logical element where a physical one is expected
		updateIfaceVLANFromLink(net, target, link, suggestedVLANs);

		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.WAN2_IFACE_DOWN);
		link = (Link) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LINK_WAN2);
		net = (IPNetworkDomain) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.WAN2);
		// Passing a logical element where a physical one is expected
		updateIfaceVLANFromLink(net, target, link, suggestedVLANs);

		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LAN_CLIENT_IFACE_UP);
		link = (Link) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LINK_LAN_CLIENT);
		net = (IPNetworkDomain) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LAN_CLIENT);
		// Passing a logical element where a physical one is expected
		updateIfaceVLANFromLink(net, target, link, suggestedVLANs);

		return logicalModel;
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

	private VCPENetworkModel suggestUnits(VCPENetworkModel physicalModel, VCPENetworkModel logicalModel) {

		VCPENetworkElement phyElement;
		IPNetworkDomain net;
		Interface target;
		Integer unit;

		SuggestedValues suggestedUnits = new SuggestedValues();

		// Suggest units from vlans

		phyElement = (Router) VCPENetworkModelHelper.getElementByTemplateName(physicalModel, TemplateConstants.ROUTER_1_PHY);

		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LR_1_IFACE_UP);
		unit = UnitSuggestor.suggestUnitFromVLAN(phyElement, target, suggestedUnits);
		target.setPort(unit);

		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LR_2_IFACE_UP);
		unit = UnitSuggestor.suggestUnitFromVLAN(phyElement, target, suggestedUnits);
		target.setPort(unit);

		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LR_CLIENT_IFACE_DOWN);
		unit = UnitSuggestor.suggestUnitFromVLAN(phyElement, target, suggestedUnits);
		target.setPort(unit);

		net = (IPNetworkDomain) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.WAN1);
		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.WAN1_IFACE_DOWN);
		unit = UnitSuggestor.suggestUnitFromVLAN(net, target, suggestedUnits);
		target.setPort(unit);

		net = (IPNetworkDomain) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.WAN2);
		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.WAN2_IFACE_DOWN);
		unit = UnitSuggestor.suggestUnitFromVLAN(net, target, suggestedUnits);
		target.setPort(unit);

		net = (IPNetworkDomain) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LAN_CLIENT);
		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LAN_CLIENT_IFACE_UP);
		unit = UnitSuggestor.suggestUnitFromVLAN(net, target, suggestedUnits);
		target.setPort(unit);

		// Suggest units for LTs
		Link lt1 = (Link) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LINK_LR_1_LR_CLIENT);
		Link lt2 = (Link) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LINK_LR_2_LR_CLIENT);

		lt1.getSource().setPort(UnitSuggestor.suggestUnit(phyElement, lt1.getSource(), suggestedUnits));
		lt1.getSink().setPort(UnitSuggestor.suggestUnit(phyElement, lt1.getSink(), suggestedUnits));

		lt2.getSource().setPort(UnitSuggestor.suggestUnit(phyElement, lt2.getSource(), suggestedUnits));
		lt2.getSink().setPort(UnitSuggestor.suggestUnit(phyElement, lt2.getSink(), suggestedUnits));

		// Suggest units for LOs
		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LR_1_IFACE_LO);
		target.setPort(UnitSuggestor.suggestUnit(phyElement, target, suggestedUnits));

		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LR_2_IFACE_LO);
		target.setPort(UnitSuggestor.suggestUnit(phyElement, target, suggestedUnits));

		target = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalModel, TemplateConstants.LR_CLIENT_IFACE_LO);
		target.setPort(UnitSuggestor.suggestUnit(phyElement, target, suggestedUnits));

		return logicalModel;
	}
}
