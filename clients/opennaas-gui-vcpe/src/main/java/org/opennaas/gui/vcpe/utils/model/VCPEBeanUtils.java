/**
 * 
 */
package org.opennaas.gui.vcpe.utils.model;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;
import org.opennaas.gui.vcpe.entities.BGP;
import org.opennaas.gui.vcpe.entities.BoD;
import org.opennaas.gui.vcpe.entities.Interface;
import org.opennaas.gui.vcpe.entities.Link;
import org.opennaas.gui.vcpe.entities.LogicalRouter;
import org.opennaas.gui.vcpe.entities.PhysicalInfrastructure;
import org.opennaas.gui.vcpe.entities.PhysicalRouter;
import org.opennaas.gui.vcpe.entities.VCPENetwork;
import org.opennaas.gui.vcpe.entities.VRRP;

/**
 * This class provides the methods to convert VCPE GUI beans to OpenNaaS beans
 * 
 * @author Jordi
 */
public class VCPEBeanUtils {

	/**
	 * Convert a OpenNaaS model to a GUI VCPENetwork model
	 * 
	 * @param openNaasModel
	 * @return VCPENetwork
	 */
	public static VCPENetwork getVCPENetwork(VCPENetworkModel modelIn) {
		VCPENetwork modelOut = new VCPENetwork();

		// Network dates
		modelOut.setId(modelIn.getId());
		modelOut.setName(modelIn.getName());
		modelOut.setClientIpRange(modelIn.getClientIpRange());
		modelOut.setNocIpRange(modelIn.getNocIpRange());
		modelOut.setTemplateType(modelIn.getTemplateType());

		// Logical Routers
		org.opennaas.extensions.vcpe.model.LogicalRouter logicalRouterMaster = (org.opennaas.extensions.vcpe.model.LogicalRouter) VCPENetworkModelHelper
				.getElementByTemplateName(modelIn, VCPETemplate.VCPE1_ROUTER);
		org.opennaas.extensions.vcpe.model.LogicalRouter logicalRouterBackup = (org.opennaas.extensions.vcpe.model.LogicalRouter) VCPENetworkModelHelper
				.getElementByTemplateName(modelIn, VCPETemplate.VCPE2_ROUTER);
		modelOut.setLogicalRouterMaster(getLogicalRouter(logicalRouterMaster));
		modelOut.setLogicalRouterBackup(getLogicalRouter(logicalRouterBackup));

		// BGP
		BGP bgp = getBGP(modelIn.getBgp());
		modelOut.setBgp(bgp);

		// BoD
		BoD bod = getBoD(modelIn);
		modelOut.setBod(bod);

		// VRRP
		modelOut.setVrrp(getVRRP(modelIn.getVrrp()));

		// Links
		List<Link> links = getLinks(VCPENetworkModelHelper.getLinks(modelIn.getElements()));
		modelOut.setLinks(links);
		return modelOut;
	}

	/**
	 * Convert a OpenNaaS model to a GUI PhysicalInfrastructure
	 * 
	 * @param openNaasModel
	 * @return VCPENetwork
	 */
	public static PhysicalInfrastructure getPhysicalInfrastructure(VCPENetworkModel modelIn) {
		PhysicalInfrastructure modelOut = new PhysicalInfrastructure();
		modelOut.setTemplateType(modelIn.getTemplateType());
		// Routers
		org.opennaas.extensions.vcpe.model.Router physicalRouterMaster = (org.opennaas.extensions.vcpe.model.Router) VCPENetworkModelHelper
				.getElementByTemplateName(modelIn, VCPETemplate.CPE1_PHY_ROUTER);
		org.opennaas.extensions.vcpe.model.Router physicalRouterBackup = (org.opennaas.extensions.vcpe.model.Router) VCPENetworkModelHelper
				.getElementByTemplateName(modelIn, VCPETemplate.CPE2_PHY_ROUTER);
		org.opennaas.extensions.vcpe.model.Router physicalRouterCore = (org.opennaas.extensions.vcpe.model.Router) VCPENetworkModelHelper
				.getElementByTemplateName(modelIn, VCPETemplate.CORE_PHY_ROUTER);

		modelOut.setPhysicalRouterMaster(getPhysicalRouter(physicalRouterMaster));
		modelOut.setPhysicalRouterBackup(getPhysicalRouter(physicalRouterBackup));
		modelOut.setPhysicalRouterCore(getPhysicalRouter(physicalRouterCore));

		return modelOut;
	}

	/**
	 * @param physicalRouterCore
	 * @return
	 */
	public static PhysicalRouter getPhysicalRouter(org.opennaas.extensions.vcpe.model.Router phyRouterIn) {
		PhysicalRouter phyRouterOut = new PhysicalRouter();
		if (phyRouterIn != null) {
			phyRouterOut.setName(phyRouterIn.getName());
			phyRouterOut.setTemplateName(phyRouterIn.getTemplateName());
			// Interfaces
			List<org.opennaas.gui.vcpe.entities.Interface> interfaces = new ArrayList<org.opennaas.gui.vcpe.entities.Interface>();
			phyRouterOut.setInterfaces(interfaces);
			for (int i = 0; i < phyRouterIn.getInterfaces().size(); i++) {
				Interface inter = getInterface(phyRouterIn.getInterfaces().get(i));
				interfaces.add(inter);
			}
		}
		return phyRouterOut;
	}

	/**
	 * Return a GUI logical router from a OpenNaaS logical router
	 * 
	 * @param inLR
	 * @return
	 */
	public static LogicalRouter getLogicalRouter(org.opennaas.extensions.vcpe.model.LogicalRouter lrIn) {
		LogicalRouter lrOut = new LogicalRouter();
		if (lrIn != null) {
			lrOut.setName(lrIn.getName());
			lrOut.setTemplateName(lrIn.getTemplateName());
			// Interfaces
			List<org.opennaas.gui.vcpe.entities.Interface> interfaces = new ArrayList<org.opennaas.gui.vcpe.entities.Interface>();
			lrOut.setInterfaces(interfaces);
			for (int i = 0; i < lrIn.getInterfaces().size(); i++) {
				Interface inter = getInterface(lrIn.getInterfaces().get(i));
				interfaces.add(inter);
			}
		}
		lrOut.setPhysicalRouter(getPhysicalRouter(lrIn.getPhysicalRouter()));
		return lrOut;
	}

	/**
	 * @param string
	 * @param interface1
	 * @return
	 */
	public static Interface getInterface(org.opennaas.extensions.vcpe.model.Interface interfaceIn) {
		Interface outIface = new Interface();
		outIface.setName(interfaceIn.getPhysicalInterfaceName());
		outIface.setTemplateName(interfaceIn.getTemplateName());
		outIface.setPort(String.valueOf(interfaceIn.getPort()));
		outIface.setIpAddress(interfaceIn.getIpAddress());
		outIface.setVlan((int) interfaceIn.getVlan());

		String templateName = interfaceIn.getTemplateName();
		if (templateName.equals(VCPETemplate.DOWN1_INTERFACE_LOCAL)
				|| templateName.equals(VCPETemplate.DOWN2_INTERFACE_LOCAL)
				|| templateName.equals(VCPETemplate.DOWN1_PHY_INTERFACE_LOCAL)
				|| templateName.equals(VCPETemplate.DOWN2_PHY_INTERFACE_LOCAL)) {
			outIface.setType(Interface.Types.DOWN.toString());
		} else if (templateName.equals(VCPETemplate.UP1_INTERFACE_LOCAL)
				|| templateName.equals(VCPETemplate.UP2_INTERFACE_LOCAL)
				|| templateName.equals(VCPETemplate.UP1_PHY_INTERFACE_LOCAL)
				|| templateName.equals(VCPETemplate.UP2_PHY_INTERFACE_LOCAL)) {
			outIface.setType(Interface.Types.UP.toString());
		} else if (templateName.equals(VCPETemplate.INTER1_INTERFACE_LOCAL)
				|| templateName.equals(VCPETemplate.INTER2_INTERFACE_LOCAL)
				|| templateName.equals(VCPETemplate.INTER1_PHY_INTERFACE_LOCAL)
				|| templateName.equals(VCPETemplate.INTER2_PHY_INTERFACE_LOCAL)) {
			outIface.setType(Interface.Types.INTER.toString());
		} else if (templateName.equals(VCPETemplate.CORE_PHY_LO_INTERFACE)
				|| templateName.equals(VCPETemplate.LO1_PHY_INTERFACE)
				|| templateName.equals(VCPETemplate.LO2_PHY_INTERFACE)
				|| templateName.equals(VCPETemplate.LO1_INTERFACE)
				|| templateName.equals(VCPETemplate.LO2_INTERFACE)) {
			outIface.setType(Interface.Types.LOOPBACK.toString());
		} else {
			outIface.setType(Interface.Types.CLIENT.toString());
		}
		return outIface;
	}

	/**
	 * Convert the OpenNaaS list links in GUI links
	 * 
	 * @param links
	 * @return list of links
	 */
	public static List<Link> getLinks(List<org.opennaas.extensions.vcpe.model.Link> inLinks) {
		List<Link> outLinks = new ArrayList<Link>();
		for (int i = 0; i < inLinks.size(); i++) {
			outLinks.add(getLink(inLinks.get(i)));
		}
		return outLinks;
	}

	/**
	 * Convert a OpenNaaS link in a GUI link
	 * 
	 * @param link
	 * @return a link
	 */
	public static Link getLink(org.opennaas.extensions.vcpe.model.Link inLink) {
		Link outlink = new Link();
		outlink.setId(inLink.getId());
		outlink.setType(inLink.getType());
		outlink.setSource(getInterface(inLink.getSource()));
		outlink.setDestination(getInterface(inLink.getSink()));
		// Implemented By
		List<Link> implementedBy = new ArrayList<Link>();
		outlink.setImplementedBy(implementedBy);
		for (int i = 0; inLink.getImplementedBy() != null && i < inLink.getImplementedBy().size(); i++) {
			implementedBy.add(getLink(inLink.getImplementedBy().get(i)));
		}
		return outlink;
	}

	/**
	 * Get the values of the BGP
	 * 
	 * @param modelIn
	 * 
	 * @return BoD
	 */
	public static BoD getBoD(VCPENetworkModel modelIn) {
		BoD bod = new BoD();
		// Interfaces
		Interface ifaceClient = getInterface((org.opennaas.extensions.vcpe.model.Interface) VCPENetworkModelHelper
				.getElementByTemplateName(modelIn.getElements(), VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN));
		Interface ifaceClientBackup = getInterface((org.opennaas.extensions.vcpe.model.Interface) VCPENetworkModelHelper
				.getElementByTemplateName(modelIn.getElements(), VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN));
		// Links
		Link linkMaster = getLink((org.opennaas.extensions.vcpe.model.Link) VCPENetworkModelHelper
				.getElementByTemplateName(modelIn.getElements(), VCPETemplate.DOWN1_LINK_AUTOBAHN));
		Link linkInter = getLink((org.opennaas.extensions.vcpe.model.Link) VCPENetworkModelHelper
				.getElementByTemplateName(modelIn.getElements(), VCPETemplate.INTER_LINK_AUTOBAHN));
		Link linkBackup = getLink((org.opennaas.extensions.vcpe.model.Link) VCPENetworkModelHelper
				.getElementByTemplateName(modelIn.getElements(), VCPETemplate.DOWN2_LINK_AUTOBAHN));

		bod.setIfaceClient(ifaceClient);
		bod.setIfaceClientBackup(ifaceClientBackup);
		bod.setLinkMaster(linkMaster);
		bod.setLinkInter(linkInter);
		bod.setLinkBackup(linkBackup);
		return bod;
	}

	/**
	 * Get the values of the BGP
	 * 
	 * @param bgp
	 * @return bgp entity
	 */
	public static BGP getBGP(org.opennaas.extensions.vcpe.model.BGP bgpIn) {
		BGP bgpOut = new BGP();
		if (bgpIn != null) {
			bgpOut.setClientASNumber(bgpIn.getClientASNumber());
			bgpOut.setNocASNumber(bgpIn.getNocASNumber());
			List<String> clientPrefixes = new ArrayList<String>();
			clientPrefixes.addAll(bgpIn.getCustomerPrefixes());
			bgpOut.setClientPrefixes(clientPrefixes);
		}
		return bgpOut;
	}

	/**
	 * Get the values of the VRRP
	 * 
	 * @param vrrp
	 * @return
	 */
	public static VRRP getVRRP(org.opennaas.extensions.vcpe.model.VRRP vrrpIn) {
		VRRP vrrpOut = new VRRP();
		if (vrrpIn != null) {
			vrrpOut.setVirtualIPAddress(vrrpIn.getVirtualIPAddress());
			vrrpOut.setPriorityMaster(vrrpIn.getPriorityMaster());
			vrrpOut.setPriorityBackup(vrrpIn.getPriorityBackup());
			vrrpOut.setGroup(vrrpIn.getGroup());
		}
		return vrrpOut;
	}

}
