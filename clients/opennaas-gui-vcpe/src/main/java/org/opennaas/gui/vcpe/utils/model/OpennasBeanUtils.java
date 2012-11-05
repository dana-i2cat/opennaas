/**
 * 
 */
package org.opennaas.gui.vcpe.utils.model;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
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
	public static VCPENetworkModel getVCPENetwork(String vcpeNetworkId, VCPENetwork vcpeNetwork) {
		VCPENetworkModel request = new VCPENetworkModel();
		// Id
		request.setVcpeNetworkId(vcpeNetworkId);
		// Name
		request.setVcpeNetworkName(vcpeNetwork.getName());
		// Template
		request.setTemplateName(vcpeNetwork.getTemplate());
		// IP Range
		request.setClientIpAddressRange(vcpeNetwork.getClientIpRange());
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
		// Interface Inter
		interfaces.add(getInterface(lrIn.getName() + "-inter", lrIn.getInterfaces().get(0)));
		// Interface Down
		interfaces.add(getInterface(lrIn.getName() + "-down", lrIn.getInterfaces().get(1)));
		// Interface Up
		interfaces.add(getInterface(lrIn.getName() + "-up", lrIn.getInterfaces().get(2)));
		return lrOut;
	}

	/**
	 * Return a OpenNaaS interface from a GUI interface
	 * 
	 * @param interface1
	 * @return Interface
	 */
	public static org.opennaas.extensions.vcpe.model.Interface getInterface(String name, Interface inIface) {
		org.opennaas.extensions.vcpe.model.Interface outIface = new org.opennaas.extensions.vcpe.model.Interface();
		outIface.setName(inIface.getCompleteName());
		outIface.setIpAddress(inIface.getIpAddress());
		outIface.setVlanId(inIface.getVlan());
		outIface.setNameInTemplate(inIface.getTemplateName());
		return outIface;
	}
}
