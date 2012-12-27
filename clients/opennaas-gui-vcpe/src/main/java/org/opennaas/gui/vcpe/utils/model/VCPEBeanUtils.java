/**
 * 
 */
package org.opennaas.gui.vcpe.utils.model;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;
import org.opennaas.gui.vcpe.entities.BGP;
import org.opennaas.gui.vcpe.entities.BoD;
import org.opennaas.gui.vcpe.entities.Interface;
import org.opennaas.gui.vcpe.entities.Link;
import org.opennaas.gui.vcpe.entities.LogicalRouter;
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
		modelOut.setId(modelIn.getVcpeNetworkId());
		modelOut.setName(modelIn.getVcpeNetworkName());
		modelOut.setClientIpRange(modelIn.getClientIpAddressRange());
		modelOut.setTemplate(modelIn.getTemplateName());
		// Logical Routers
		Router logicalRouter1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(modelIn, VCPETemplate.VCPE1_ROUTER);
		Router logicalRouter2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(modelIn, VCPETemplate.VCPE2_ROUTER);
		modelOut.setLogicalRouter1(getLogicalRouter(logicalRouter1));
		modelOut.setLogicalRouter2(getLogicalRouter(logicalRouter2));
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
	 * Return a GUI logical router from a OpenNaaS logical router
	 * 
	 * @param inLR
	 * @return
	 */
	public static LogicalRouter getLogicalRouter(Router lrIn) {
		LogicalRouter lrOut = new LogicalRouter();
		if (lrIn != null) {
			lrOut.setName(lrIn.getName());
			lrOut.setTemplateName(lrIn.getNameInTemplate());
			// Interfaces
			List<org.opennaas.gui.vcpe.entities.Interface> interfaces = new ArrayList<org.opennaas.gui.vcpe.entities.Interface>();
			lrOut.setInterfaces(interfaces);
			// Interface Inter
			Interface inter = getInterface(lrIn.getInterfaces().get(0));
			inter.setLabelName(Interface.Types.INTER.toString());
			interfaces.add(inter);
			// Interface Down
			Interface down = getInterface(lrIn.getInterfaces().get(1));
			down.setLabelName(Interface.Types.DOWN.toString());
			interfaces.add(down);
			// Interface Up
			Interface up = getInterface(lrIn.getInterfaces().get(2));
			up.setLabelName(Interface.Types.UP.toString());
			interfaces.add(up);
		}
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
		outIface.setTemplateName(interfaceIn.getNameInTemplate());
		outIface.setPort(String.valueOf(interfaceIn.getPortNumber()));
		outIface.setIpAddress(interfaceIn.getIpAddress());
		outIface.setVlan((int) interfaceIn.getVlanId());
		if (interfaceIn.getNameInTemplate().equals(VCPETemplate.DOWN1_INTERFACE_LOCAL)
				|| interfaceIn.getNameInTemplate().equals(VCPETemplate.DOWN2_INTERFACE_LOCAL)) {
			outIface.setLabelName(Interface.Types.DOWN.toString());
		} else if (interfaceIn.getNameInTemplate().equals(VCPETemplate.UP1_INTERFACE_LOCAL)
				|| interfaceIn.getNameInTemplate().equals(VCPETemplate.UP2_INTERFACE_LOCAL)) {
			outIface.setLabelName(Interface.Types.UP.toString());
		} else if (interfaceIn.getNameInTemplate().equals(VCPETemplate.INTER1_INTERFACE_LOCAL)
				|| interfaceIn.getNameInTemplate().equals(VCPETemplate.INTER2_INTERFACE_LOCAL)) {
			outIface.setLabelName(Interface.Types.INTER.toString());
		} else {
			outIface.setLabelName(Interface.Types.CLIENT.toString());
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
	private static BoD getBoD(VCPENetworkModel modelIn) {
		BoD bod = new BoD();
		// Interfaces
		Interface ifaceClient = getInterface((org.opennaas.extensions.vcpe.model.Interface) VCPENetworkModelHelper
				.getElementByNameInTemplate(modelIn.getElements(), VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN));
		Interface ifaceClientBackup = getInterface((org.opennaas.extensions.vcpe.model.Interface) VCPENetworkModelHelper
				.getElementByNameInTemplate(modelIn.getElements(), VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN));
		// Links
		Link linkMaster = getLink((org.opennaas.extensions.vcpe.model.Link) VCPENetworkModelHelper
				.getElementByNameInTemplate(modelIn.getElements(), VCPETemplate.DOWN1_LINK_AUTOBAHN));
		Link linkInter = getLink((org.opennaas.extensions.vcpe.model.Link) VCPENetworkModelHelper
				.getElementByNameInTemplate(modelIn.getElements(), VCPETemplate.INTER_LINK_AUTOBAHN));
		Link linkBackup = getLink((org.opennaas.extensions.vcpe.model.Link) VCPENetworkModelHelper
				.getElementByNameInTemplate(modelIn.getElements(), VCPETemplate.DOWN2_LINK_AUTOBAHN));

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
	private static BGP getBGP(org.opennaas.extensions.vcpe.model.BGP bgpIn) {
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
	private static VRRP getVRRP(org.opennaas.extensions.vcpe.model.VRRP vrrpIn) {
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
