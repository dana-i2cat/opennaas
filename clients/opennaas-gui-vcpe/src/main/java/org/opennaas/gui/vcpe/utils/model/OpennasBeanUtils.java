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
import org.opennaas.gui.vcpe.entities.VRRP;

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
	public static VCPENetworkModel getVCPENetwork(VCPENetwork modelIn) {
		VCPENetworkModel modelOut = new VCPENetworkModel();
		// Id
		modelOut.setId(modelIn.getId());
		// Name
		modelOut.setName(modelIn.getName());
		// Template
		modelOut.setTemplateType(modelIn.getTemplateType());
		// IP Range
		modelOut.setClientIpRange(modelIn.getClientIpRange());
		// BGP
		if (modelIn.getBgp() != null) {
			modelOut.setBgp(getBGP(modelIn.getBgp()));
		}
		// VRRP
		if (modelIn.getVrrp() != null) {
			modelOut.setVrrp(getVRRP(modelIn.getVrrp()));
		}
		// Elements
		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		modelOut.setElements(elements);
		// LogicalRouters
		org.opennaas.extensions.vcpe.model.LogicalRouter logicalRouter1 = getLROpennaas(modelIn.getName(), VCPETemplate.VCPE1_ROUTER,
				modelIn.getLogicalRouter1());
		org.opennaas.extensions.vcpe.model.LogicalRouter logicalRouter2 = getLROpennaas(modelIn.getName(), VCPETemplate.VCPE2_ROUTER,
				modelIn.getLogicalRouter2());
		elements.add(logicalRouter1);
		elements.add(logicalRouter2);
		// Add interfaces to elements
		elements.addAll(logicalRouter1.getInterfaces());
		elements.addAll(logicalRouter2.getInterfaces());
		// Add interfaces BoD
		if (modelIn.getBod() != null) {
			elements.add(getInterface(modelIn.getBod().getIfaceClient()));
			elements.add(getInterface(modelIn.getBod().getIfaceClientBackup()));
		}
		return modelOut;
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
		lrOut.setTemplateName(templateName);
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
		outIface.setVlan(inIface.getVlan());
		outIface.setTemplateName(inIface.getTemplateName());
		outIface.setPhysicalInterfaceName(inIface.getName());
		outIface.setPort(Integer.parseInt(inIface.getPort()));
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
		List<String> clientPrefixes = new ArrayList<String>();
		clientPrefixes.addAll(bgpIn.getClientPrefixes());
		bgpOut.setCustomerPrefixes(clientPrefixes);
		return bgpOut;
	}

	/**
	 * Get the values of OpenNaaS VRRP from GUI VRRP
	 * 
	 * @param vrrp
	 * @return vrrp entity
	 */
	private static org.opennaas.extensions.vcpe.model.VRRP getVRRP(VRRP vrrpIn) {
		org.opennaas.extensions.vcpe.model.VRRP vrrpOut = new org.opennaas.extensions.vcpe.model.VRRP();
		vrrpOut.setVirtualIPAddress(vrrpIn.getVirtualIPAddress());
		vrrpOut.setPriorityMaster(vrrpIn.getPriorityMaster());
		vrrpOut.setPriorityBackup(vrrpIn.getPriorityBackup());
		vrrpOut.setGroup(vrrpIn.getGroup());
		return vrrpOut;
	}
}
