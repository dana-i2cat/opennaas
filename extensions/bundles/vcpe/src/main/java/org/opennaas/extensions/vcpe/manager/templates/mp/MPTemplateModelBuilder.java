package org.opennaas.extensions.vcpe.manager.templates.mp;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.IPNetworkDomain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.LogicalRouter;
import org.opennaas.extensions.vcpe.model.PhysicalRouter;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class MPTemplateModelBuilder {

	/**
	 * 
	 * @return a newly created VCPENetworkModel with the structure required for MP-vCPE template.
	 */
	public static VCPENetworkModel generateModel() {
		return mapLogicalAndPhysical(generatePhysicalElements(), generateLogicalElements());
	}

	/**
	 * 
	 * @return a newly created VCPENetworkModel containing all physical elements required for MP-vCPE template.
	 */
	public static VCPENetworkModel generatePhysicalElements() {

		List<Interface> interfaces;

		// create networks
		IPNetworkDomain wan1 = new IPNetworkDomain();
		wan1.setTemplateName(TemplateConstants.WAN1);

		IPNetworkDomain wan2 = new IPNetworkDomain();
		wan2.setTemplateName(TemplateConstants.WAN2);

		IPNetworkDomain client = new IPNetworkDomain();
		client.setTemplateName(TemplateConstants.LAN_CLIENT);

		// create interfaces for networks
		Interface wan1down = createInterface(TemplateConstants.WAN1_PHY_IFACE_DOWN);
		Interface wan2down = createInterface(TemplateConstants.WAN2_PHY_IFACE_DOWN);
		Interface clientLanUp = createInterface(TemplateConstants.LAN_CLIENT_PHY_IFACE_UP);

		interfaces = new ArrayList<Interface>();
		interfaces.add(wan1down);
		wan1.setInterfaces(interfaces);

		interfaces = new ArrayList<Interface>();
		interfaces.add(wan2down);
		wan2.setInterfaces(interfaces);

		interfaces = new ArrayList<Interface>();
		interfaces.add(clientLanUp);
		client.setInterfaces(interfaces);

		// LogicalRouter 1
		Router lr1 = new PhysicalRouter();
		lr1.setTemplateName(TemplateConstants.LR_1_PHY_ROUTER);

		interfaces = new ArrayList<Interface>();
		lr1.setInterfaces(interfaces);

		Interface lr1Up = createInterface(TemplateConstants.LR_1_PHY_IFACE_UP);
		Interface lr1Down = createInterface(TemplateConstants.LR_1_PHY_IFACE_DOWN);
		Interface lr1Lo = createInterface(TemplateConstants.LR_1_PHY_IFACE_LO);
		interfaces.add(lr1Up);
		interfaces.add(lr1Down);
		interfaces.add(lr1Lo);

		// LogicalRouter 2
		Router lr2 = new PhysicalRouter();
		lr2.setTemplateName(TemplateConstants.LR_2_PHY_ROUTER);

		interfaces = new ArrayList<Interface>();
		lr2.setInterfaces(interfaces);

		Interface lr2Up = createInterface(TemplateConstants.LR_2_PHY_IFACE_UP);
		Interface lr2Down = createInterface(TemplateConstants.LR_2_PHY_IFACE_DOWN);
		Interface lr2Lo = createInterface(TemplateConstants.LR_2_PHY_IFACE_LO);
		interfaces.add(lr2Up);
		interfaces.add(lr2Down);
		interfaces.add(lr2Lo);

		// Client Logical Router
		Router lrclient = new PhysicalRouter();
		lrclient.setTemplateName(TemplateConstants.LR_CLIENT_PHY_ROUTER);

		interfaces = new ArrayList<Interface>();
		lrclient.setInterfaces(interfaces);

		Interface lrclientUp1 = createInterface(TemplateConstants.LR_CLIENT_PHY_IFACE_UP1);
		Interface lrclientUp2 = createInterface(TemplateConstants.LR_CLIENT_PHY_IFACE_UP2);
		Interface lrclientDown = createInterface(TemplateConstants.LR_CLIENT_PHY_IFACE_DOWN);
		Interface lrclientLo = createInterface(TemplateConstants.LR_CLIENT_PHY_IFACE_LO);
		interfaces.add(lrclientUp1);
		interfaces.add(lrclientUp2);
		interfaces.add(lrclientDown);
		interfaces.add(lrclientLo);

		// TODO link type should be an enumeration in Link
		// Links
		Link wan1Link = VCPENetworkModelHelper.createLink(null, TemplateConstants.LINK_WAN1_PHY, "eth", lr1Up, wan1down);
		Link wan2Link = VCPENetworkModelHelper.createLink(null, TemplateConstants.LINK_WAN2_PHY, "eth", lr2Up, wan2down);
		Link clientLink = VCPENetworkModelHelper.createLink(null, TemplateConstants.LINK_LAN_CLIENT_PHY, "eth", lrclientDown, clientLanUp);

		Link intern1Link = VCPENetworkModelHelper.createLink(null, TemplateConstants.LINK_LR_1_LR_CLIENT_PHY, "lt", lr1Down, lrclientUp1);
		Link intern2Link = VCPENetworkModelHelper.createLink(null, TemplateConstants.LINK_LR_2_LR_CLIENT_PHY, "lt", lr2Down, lrclientUp2);

		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		elements.add(wan1);
		elements.addAll(wan1.getInterfaces());
		elements.add(wan2);
		elements.addAll(wan2.getInterfaces());
		elements.add(client);
		elements.addAll(client.getInterfaces());
		elements.add(lr1);
		elements.addAll(lr1.getInterfaces());
		elements.add(lr2);
		elements.addAll(lr2.getInterfaces());
		elements.add(lrclient);
		elements.addAll(lrclient.getInterfaces());
		elements.add(wan1Link);
		elements.add(wan2Link);
		elements.add(clientLink);
		elements.add(intern1Link);
		elements.add(intern2Link);

		VCPENetworkModel model = new VCPENetworkModel();
		model.setElements(elements);
		model.setTemplateType(MultipleProviderTemplate.MP_VCPE_TEMPLATE);
		model.setCreated(false);
		return model;

	}

	/**
	 * 
	 * @return a newly created VCPENetworkModel containing all logical elements required for MP-vCPE template.
	 */
	public static VCPENetworkModel generateLogicalElements() {

		Interface wan1down = createInterface(TemplateConstants.WAN1_IFACE_DOWN);
		Interface wan2down = createInterface(TemplateConstants.WAN2_IFACE_DOWN);
		Interface clientLanUp = createInterface(TemplateConstants.LAN_CLIENT_IFACE_UP);

		// LogicalRouter 1
		Router lr1 = new LogicalRouter();
		lr1.setTemplateName(TemplateConstants.LR_1_ROUTER);

		List<Interface> interfaces = new ArrayList<Interface>();
		lr1.setInterfaces(interfaces);

		Interface lr1Up = createInterface(TemplateConstants.LR_1_IFACE_UP);
		Interface lr1Down = createInterface(TemplateConstants.LR_1_IFACE_DOWN);
		Interface lr1Lo = createInterface(TemplateConstants.LR_1_IFACE_LO);
		interfaces.add(lr1Up);
		interfaces.add(lr1Down);
		interfaces.add(lr1Lo);

		// LogicalRouter 2
		Router lr2 = new LogicalRouter();
		lr2.setTemplateName(TemplateConstants.LR_2_ROUTER);

		interfaces = new ArrayList<Interface>();
		lr2.setInterfaces(interfaces);

		Interface lr2Up = createInterface(TemplateConstants.LR_2_IFACE_UP);
		Interface lr2Down = createInterface(TemplateConstants.LR_2_IFACE_DOWN);
		Interface lr2Lo = createInterface(TemplateConstants.LR_2_IFACE_LO);
		interfaces.add(lr2Up);
		interfaces.add(lr2Down);
		interfaces.add(lr2Lo);

		// Client Logical Router
		Router lrclient = new LogicalRouter();
		lrclient.setTemplateName(TemplateConstants.LR_CLIENT_ROUTER);

		interfaces = new ArrayList<Interface>();
		lrclient.setInterfaces(interfaces);

		Interface lrclientUp1 = createInterface(TemplateConstants.LR_CLIENT_IFACE_UP1);
		Interface lrclientUp2 = createInterface(TemplateConstants.LR_CLIENT_IFACE_UP2);
		Interface lrclientDown = createInterface(TemplateConstants.LR_CLIENT_IFACE_DOWN);
		Interface lrclientLo = createInterface(TemplateConstants.LR_CLIENT_IFACE_LO);
		interfaces.add(lrclientUp1);
		interfaces.add(lrclientUp2);
		interfaces.add(lrclientDown);
		interfaces.add(lrclientLo);

		// Links
		Link wan1Link = VCPENetworkModelHelper.createLink(null, TemplateConstants.LINK_WAN1, "eth", lr1Up, wan1down);
		Link wan2Link = VCPENetworkModelHelper.createLink(null, TemplateConstants.LINK_WAN2, "eth", lr2Up, wan2down);
		Link clientLink = VCPENetworkModelHelper.createLink(null, TemplateConstants.LINK_LAN_CLIENT, "eth", lrclientDown, clientLanUp);

		Link intern1Link = VCPENetworkModelHelper.createLink(null, TemplateConstants.LINK_LR_1_LR_CLIENT, "lt", lr1Down, lrclientUp1);
		Link intern2Link = VCPENetworkModelHelper.createLink(null, TemplateConstants.LINK_LR_2_LR_CLIENT, "lt", lr2Down, lrclientUp2);

		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		elements.add(wan1down);
		elements.add(wan2down);
		elements.add(clientLanUp);
		elements.add(lr1);
		elements.addAll(lr1.getInterfaces());
		elements.add(lr2);
		elements.addAll(lr2.getInterfaces());
		elements.add(lrclient);
		elements.addAll(lrclient.getInterfaces());
		elements.add(wan1Link);
		elements.add(wan2Link);
		elements.add(clientLink);
		elements.add(intern1Link);
		elements.add(intern2Link);

		VCPENetworkModel model = new VCPENetworkModel();
		model.setElements(elements);
		model.setTemplateType(MultipleProviderTemplate.MP_VCPE_TEMPLATE);
		model.setCreated(false);
		return model;

	}

	/**
	 * Joins given models into one, and adds required relationships between logical elements and physical ones.
	 * 
	 * @param physicalInfrastructure
	 * @param logicalInfrastructure
	 * @return given logialInfrasctucture updated with all elements in physicalInfrastructure and with new relationships added.
	 */
	public static VCPENetworkModel mapLogicalAndPhysical(VCPENetworkModel physicalInfrastructure, VCPENetworkModel logicalInfrastructure) {

		// put all physical elements in logicalInfrastructure
		logicalInfrastructure.getElements().addAll(physicalInfrastructure.getElements());

		// put logical interfaces into corresponding networks
		Domain net;
		Interface iface;

		net = (Domain) VCPENetworkModelHelper.getElementByTemplateName(physicalInfrastructure, TemplateConstants.WAN1);
		iface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, TemplateConstants.WAN1_IFACE_DOWN);
		net.getInterfaces().add(iface);

		net = (Domain) VCPENetworkModelHelper.getElementByTemplateName(physicalInfrastructure, TemplateConstants.WAN2);
		iface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, TemplateConstants.WAN2_IFACE_DOWN);
		net.getInterfaces().add(iface);

		net = (Domain) VCPENetworkModelHelper.getElementByTemplateName(physicalInfrastructure, TemplateConstants.LAN_CLIENT);
		iface = (Interface) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, TemplateConstants.LAN_CLIENT_IFACE_UP);
		net.getInterfaces().add(iface);

		// Assign logical routers to physical ones
		Router phyRouter;
		LogicalRouter logRouter;

		phyRouter = (Router) VCPENetworkModelHelper.getElementByTemplateName(physicalInfrastructure, TemplateConstants.LR_1_PHY_ROUTER);
		logRouter = (LogicalRouter) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, TemplateConstants.LR_1_ROUTER);
		logRouter.setPhysicalRouter(phyRouter);

		phyRouter = (Router) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, TemplateConstants.LR_2_PHY_ROUTER);
		logRouter = (LogicalRouter) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, TemplateConstants.LR_2_ROUTER);
		logRouter.setPhysicalRouter(phyRouter);

		phyRouter = (Router) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, TemplateConstants.LR_CLIENT_PHY_ROUTER);
		logRouter = (LogicalRouter) VCPENetworkModelHelper.getElementByTemplateName(logicalInfrastructure, TemplateConstants.LR_CLIENT_ROUTER);
		logRouter.setPhysicalRouter(phyRouter);

		return logicalInfrastructure;
	}

	private static Interface createInterface(String nameInTemplate) {
		Interface iface = new Interface();
		iface.setTemplateName(nameInTemplate);
		return iface;
	}

}
