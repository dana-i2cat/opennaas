/**
 * 
 */
package org.opennaas.gui.vcpe.utils.model;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.gui.vcpe.entities.BGP;
import org.opennaas.gui.vcpe.entities.Interface;
import org.opennaas.gui.vcpe.entities.LogicalRouter;
import org.opennaas.gui.vcpe.entities.VCPENetwork;

/**
 * This class provides the methods to convert OpenNaaS beans to VCPE GUI beans
 * 
 * @author Jordi
 */
public class OpennasBeanUtils {

	/**
	 * Get params to call the ws to create the VCPENetwork enviroment
	 * 
	 * @param vcpeNetworkId
	 * 
	 * @param vcpeNetwork
	 * @return CreateVCPENetRequest
	 */
	public static VCPENetworkModel getVCPENetwork(VCPENetwork vcpeNetwork) {
		VCPENetworkModel request = new VCPENetworkModel();
		// Id
		request.setVcpeNetworkId(vcpeNetwork.getId());
		// Name
		request.setVcpeNetworkName(vcpeNetwork.getName());
		// Template
		request.setTemplateName(vcpeNetwork.getTemplate());
		// IP Range
		request.setClientIpAddressRange(vcpeNetwork.getClientIpRange());
		// BGP
		request.setBgp(getBGP(vcpeNetwork.getBgp()));
		// Elements
		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		request.setElements(elements);
		// LogicalRouters
		org.opennaas.extensions.vcpe.model.LogicalRouter logicalRouter1 = getLROpennaas(vcpeNetwork.getName(), VCPETemplate.VCPE1_ROUTER,
				vcpeNetwork.getLogicalRouter1());
		org.opennaas.extensions.vcpe.model.LogicalRouter logicalRouter2 = getLROpennaas(vcpeNetwork.getName(), VCPETemplate.VCPE2_ROUTER,
				vcpeNetwork.getLogicalRouter2());
		elements.add(logicalRouter1);
		elements.add(logicalRouter2);
		// Add interfaces to elements
		elements.addAll(logicalRouter1.getInterfaces());
		elements.addAll(logicalRouter2.getInterfaces());
		return request;
	}

	/**
	 * Return a OpenNaaS logical router from a GUI logical router
	 * 
	 * @param networkName
	 * @param logicalRouter1
	 * @return LogicalRouter of opennaas model
	 */
	public static org.opennaas.extensions.vcpe.model.LogicalRouter getLROpennaas(String networkName, String templateName, LogicalRouter lrIn) {
		org.opennaas.extensions.vcpe.model.LogicalRouter lrOut = new org.opennaas.extensions.vcpe.model.LogicalRouter();
		lrOut.setName(lrIn.getName());
		lrOut.setNameInTemplate(templateName);
		// Interfaces
		List<org.opennaas.extensions.vcpe.model.Interface> interfaces = new ArrayList<org.opennaas.extensions.vcpe.model.Interface>();
		lrOut.setInterfaces(interfaces);
		for (int i = 0; i < lrIn.getInterfaces().size(); i++) {
			interfaces.add(getInterface(lrIn.getInterfaces().get(i)));
		}
		return lrOut;
	}

	/**
	 * Return a OpenNaaS interface from a GUI interface
	 * 
	 * @param interface1
	 * @return Interface
	 */
	public static org.opennaas.extensions.vcpe.model.Interface getInterface(Interface inIface) {
		org.opennaas.extensions.vcpe.model.Interface outIface = new org.opennaas.extensions.vcpe.model.Interface();
		outIface.setName(inIface.getCompleteName());
		outIface.setIpAddress(inIface.getIpAddress());
		outIface.setVlanId(inIface.getVlan());
		outIface.setNameInTemplate(inIface.getTemplateName());
		return outIface;
	}

	/**
	 * Get the values of OpenNaaS BGP from GUI BGP
	 * 
	 * @param bgp
	 * @return bgp entity
	 */
	private static org.opennaas.extensions.vcpe.model.BGP getBGP(BGP bgpIn) {
		org.opennaas.extensions.vcpe.model.BGP bgpOut = new org.opennaas.extensions.vcpe.model.BGP();
		bgpOut.setClientASNumber(bgpIn.getClientASNumber());
		bgpOut.setNocASNumber(bgpIn.getNocASNumber());
		List<String> customerPrefixes = new ArrayList<String>();
		customerPrefixes.addAll(bgpIn.getCustomerPrefixes());
		bgpOut.setCustomerPrefixes(customerPrefixes);
		return bgpOut;
	}
}
