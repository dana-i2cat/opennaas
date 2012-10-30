/**
 * 
 */
package org.opennaas.web.utils.model;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;
import org.opennaas.web.entities.Interface;
import org.opennaas.web.entities.Link;
import org.opennaas.web.entities.LogicalRouter;
import org.opennaas.web.entities.VCPENetwork;

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
		Router logicalRouter1 = (Router) VCPENetworkModelHelper
				.getElementByNameInTemplate(modelIn, VCPETemplate.VCPE1_ROUTER);
		Router logicalRouter2 = (Router) VCPENetworkModelHelper
				.getElementByNameInTemplate(modelIn, VCPETemplate.VCPE2_ROUTER);

		modelOut.setLogicalRouter1(getLogicalRouter(logicalRouter1));
		modelOut.setLogicalRouter2(getLogicalRouter(logicalRouter2));

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
			List<org.opennaas.web.entities.Interface> interfaces = new ArrayList<org.opennaas.web.entities.Interface>();
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
		org.opennaas.web.entities.Interface outIface = new org.opennaas.web.entities.Interface();
		outIface.setName(org.opennaas.web.entities.Interface.getNameFromCompleteName(interfaceIn.getName()));
		outIface.setTemplateName(interfaceIn.getNameInTemplate());
		outIface.setPort(org.opennaas.web.entities.Interface.getPortFromCompleteName(interfaceIn.getName()));
		outIface.setIpAddress(interfaceIn.getIpAddress());
		outIface.setVlan((int) interfaceIn.getVlanId());
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
		// TODO
		Link outlink = new Link();
		return outlink;
	}
}
