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
import org.opennaas.gui.vcpe.entities.PhysicalInfrastructure;
import org.opennaas.gui.vcpe.entities.PhysicalRouter;
import org.opennaas.gui.vcpe.entities.VCPENetwork;
import org.opennaas.gui.vcpe.entities.VRRP;

/**
 * This class provides the methods to convert OpenNaaS beans to VCPE GUI beans
 * 
 * @author Jordi
 */
public class OpennaasBeanUtils {

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
		// Client IP Range
		modelOut.setClientIpRange(modelIn.getClientIpRange());
		// NOC IP Range
		modelOut.setNocIpRange(modelIn.getNocIpRange());
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
		org.opennaas.extensions.vcpe.model.Router logicalRouterMaster = getLROpennaas(modelIn.getName(), VCPETemplate.VCPE1_ROUTER,
				modelIn.getLogicalRouterMaster());
		org.opennaas.extensions.vcpe.model.Router logicalRouterBackup = getLROpennaas(modelIn.getName(), VCPETemplate.VCPE2_ROUTER,
				modelIn.getLogicalRouterBackup());
		elements.add(logicalRouterMaster);
		elements.add(logicalRouterBackup);
		// Add interfaces to elements
		elements.addAll(logicalRouterMaster.getInterfaces());
		elements.addAll(logicalRouterBackup.getInterfaces());
		// Add interfaces BoD
		if (modelIn.getBod() != null) {
			elements.add(getLogicalInterface(modelIn.getBod().getIfaceClient()));
			elements.add(getLogicalInterface(modelIn.getBod().getIfaceClientBackup()));
		}
		return modelOut;
	}

	/**
	 * Return a OpenNaaS logical router from a GUI logical router
	 * 
	 * @param networkName
	 * @param logicalRouterMaster
	 * @return LogicalRouter of opennaas model
	 */
	public static org.opennaas.extensions.vcpe.model.Router getLROpennaas(String networkName, String templateName, LogicalRouter lrIn) {
		org.opennaas.extensions.vcpe.model.Router lrOut = new org.opennaas.extensions.vcpe.model.Router();
		lrOut.setName(lrIn.getName());
		lrOut.setTemplateName(templateName);
		// Interfaces
		List<org.opennaas.extensions.vcpe.model.Interface> interfaces = new ArrayList<org.opennaas.extensions.vcpe.model.Interface>();
		lrOut.setInterfaces(interfaces);
		for (int i = 0; i < lrIn.getInterfaces().size(); i++) {
			interfaces.add(getLogicalInterface(lrIn.getInterfaces().get(i)));
		}
		return lrOut;
	}

	/**
	 * Return a OpenNaaS logical interface from a GUI interface
	 * 
	 * @param interface1
	 * @return Interface
	 */
	public static org.opennaas.extensions.vcpe.model.Interface getLogicalInterface(Interface inIface) {
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
	 * Return a OpenNaaS physical from a GUI interface
	 * 
	 * @param interface1
	 * @return Interface
	 */
	public static org.opennaas.extensions.vcpe.model.Interface getPhysicalInterface(Interface inIface) {
		org.opennaas.extensions.vcpe.model.Interface outIface = new org.opennaas.extensions.vcpe.model.Interface();
		outIface.setName(inIface.getName());
		outIface.setTemplateName(inIface.getTemplateName());
		outIface.setPhysicalInterfaceName(inIface.getName());
		return outIface;
	}

	/**
	 * Get the values of OpenNaaS BGP from GUI BGP
	 * 
	 * @param bgp
	 * @return bgp entity
	 */
	public static org.opennaas.extensions.vcpe.model.BGP getBGP(BGP bgpIn) {
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
	public static org.opennaas.extensions.vcpe.model.VRRP getVRRP(VRRP vrrpIn) {
		org.opennaas.extensions.vcpe.model.VRRP vrrpOut = new org.opennaas.extensions.vcpe.model.VRRP();
		vrrpOut.setVirtualIPAddress(vrrpIn.getVirtualIPAddress());
		vrrpOut.setPriorityMaster(vrrpIn.getPriorityMaster());
		vrrpOut.setPriorityBackup(vrrpIn.getPriorityBackup());
		vrrpOut.setGroup(vrrpIn.getGroup());
		return vrrpOut;
	}

	/**
	 * Convert a OpenNaaS model to a GUI VCPEPhysicalNetwork model
	 * 
	 * @param openNaasModel
	 * @return VCPENetwork
	 */
	public static VCPENetworkModel getPhysicalInfrastructure(PhysicalInfrastructure modelIn) {
		VCPENetworkModel modelOut = new VCPENetworkModel();
		modelOut.setTemplateType(modelIn.getTemplateType());
		// Routers Master
		org.opennaas.extensions.vcpe.model.Router physicalRouterMaster = getPhysicalRouter(modelIn.getPhysicalRouterMaster());
		physicalRouterMaster.setTemplateName(VCPETemplate.CPE1_PHY_ROUTER);
		// Routers Backup
		org.opennaas.extensions.vcpe.model.Router physicalRouterBackup = getPhysicalRouter(modelIn.getPhysicalRouterBackup());
		physicalRouterMaster.setTemplateName(VCPETemplate.CPE2_PHY_ROUTER);
		// Routers Core
		org.opennaas.extensions.vcpe.model.Router physicalRouterCore = getPhysicalRouter(modelIn.getPhysicalRouterBackup());
		physicalRouterMaster.setTemplateName(VCPETemplate.CORE_PHY_ROUTER);

		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		elements.add(physicalRouterMaster);
		elements.add(physicalRouterBackup);
		elements.add(physicalRouterCore);
		elements.addAll(physicalRouterMaster.getInterfaces());
		elements.addAll(physicalRouterBackup.getInterfaces());
		elements.addAll(physicalRouterCore.getInterfaces());
		modelOut.setElements(elements);

		return modelOut;
	}

	/**
	 * @param physicalRouterCore
	 * @return
	 */
	public static org.opennaas.extensions.vcpe.model.Router getPhysicalRouter(PhysicalRouter phyRouterIn) {
		org.opennaas.extensions.vcpe.model.Router phyRouterOut = new org.opennaas.extensions.vcpe.model.Router();
		if (phyRouterIn != null) {
			phyRouterOut.setName(phyRouterIn.getName());
			phyRouterOut.setTemplateName(phyRouterIn.getTemplateName());
			// Interfaces
			List<org.opennaas.extensions.vcpe.model.Interface> interfaces = new ArrayList<org.opennaas.extensions.vcpe.model.Interface>();
			phyRouterOut.setInterfaces(interfaces);
			for (int i = 0; i < phyRouterIn.getInterfaces().size(); i++) {
				org.opennaas.extensions.vcpe.model.Interface inter = getPhysicalInterface(phyRouterIn.getInterfaces().get(i));
				interfaces.add(inter);
			}
		}
		return phyRouterOut;
	}
}
