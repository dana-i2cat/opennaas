/**
 * 
 */
package org.opennaas.gui.vcpe.utils.model;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.vcpe.manager.templates.sp.SPTemplateConstants;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.gui.vcpe.entities.BGP;
import org.opennaas.gui.vcpe.entities.Interface;
import org.opennaas.gui.vcpe.entities.LogicalInfrastructure;
import org.opennaas.gui.vcpe.entities.MultipleProviderPhysical;
import org.opennaas.gui.vcpe.entities.PhysicalInfrastructure;
import org.opennaas.gui.vcpe.entities.Router;
import org.opennaas.gui.vcpe.entities.SingleProviderLogical;
import org.opennaas.gui.vcpe.entities.SingleProviderPhysical;
import org.opennaas.gui.vcpe.entities.VRRP;
import org.opennaas.gui.vcpe.enums.Template;

/**
 * This class provides the methods to convert OpenNaaS beans to VCPE GUI beans
 * 
 * @author Jordi
 */
public class OpennaasBeanUtils {

	/**
	 * Convert a OpenNaaS model to a GUI VCPENetwork model
	 * 
	 * @param openNaasModel
	 * @return VCPENetwork
	 */
	public static VCPENetworkModel getVCPENetwork(LogicalInfrastructure modelIn) {
		VCPENetworkModel modelOut = null;
		if (modelIn.getTemplateType().equals(Template.SINGLE_PROVIDER.toString())) {
			modelOut = getVCPENetworkFromSP((SingleProviderLogical) modelIn);
		} else if (modelIn.getTemplateType().equals(Template.MULTIPLE_PROVIDER.toString())) {
			modelOut = getVCPENetworkFromMP((MultipleProviderPhysical) modelIn);
		}
		return modelOut;
	}

	/**
	 * Get params to call the ws to create the VCPENetwork enviroment
	 * 
	 * @param vcpeNetworkId
	 * 
	 * @param vcpeNetwork
	 * @return CreateVCPENetRequest
	 */
	public static VCPENetworkModel getVCPENetworkFromSP(SingleProviderLogical modelIn) {
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
		// Core Router
		org.opennaas.extensions.vcpe.model.Router routerCore = getRouterOpennaas(modelIn.getRouterCore());
		// LogicalRouters
		org.opennaas.extensions.vcpe.model.Router logicalRouterMaster = getRouterOpennaas(modelIn.getLogicalRouterMaster());
		org.opennaas.extensions.vcpe.model.Router logicalRouterBackup = getRouterOpennaas(modelIn.getLogicalRouterBackup());

		elements.add(routerCore);
		elements.add(logicalRouterMaster);
		elements.add(logicalRouterBackup);
		// Add interfaces to elements
		elements.addAll(logicalRouterMaster.getInterfaces());
		elements.addAll(logicalRouterBackup.getInterfaces());
		elements.addAll(routerCore.getInterfaces());
		// Add interfaces BoD
		if (modelIn.getBod() != null) {
			elements.add(getInterfaceOpennaas(modelIn.getBod().getIfaceClient()));
			elements.add(getInterfaceOpennaas(modelIn.getBod().getIfaceClientBackup()));
		}
		return modelOut;
	}

	/**
	 * Get params to call the ws to create the VCPENetwork enviroment
	 * 
	 * @param vcpeNetworkId
	 * 
	 * @param vcpeNetwork
	 * @return CreateVCPENetRequest
	 */
	public static VCPENetworkModel getVCPENetworkFromMP(MultipleProviderPhysical modelIn) {
		// TODO Auto-generated method stub

		return null;
	}

	/**
	 * Convert a OpenNaaS model to a GUI VCPEPhysicalNetwork model
	 * 
	 * @param openNaasModel
	 * @return VCPENetwork
	 */
	public static VCPENetworkModel getPhysicalInfrastructure(PhysicalInfrastructure modelIn) {
		VCPENetworkModel modelOut = null;
		if (modelIn.getTemplateType().equals(Template.SINGLE_PROVIDER.toString())) {
			modelOut = getSingleProviderPhysical((SingleProviderPhysical) modelIn);
		} else if (modelIn.getTemplateType().equals(Template.MULTIPLE_PROVIDER.toString())) {
			modelOut = getMultipleProviderPhysical();
		}
		return modelOut;
	}

	/**
	 * Convert a GUI model to a Opennaas physical multiple provider model
	 * 
	 * @return
	 */
	private static VCPENetworkModel getMultipleProviderPhysical() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Convert a GUI model to a Opennaas physical simple provider model
	 * 
	 * @param modelIn
	 * @return
	 */
	private static VCPENetworkModel getSingleProviderPhysical(SingleProviderPhysical modelIn) {
		VCPENetworkModel modelOut = new VCPENetworkModel();
		modelOut.setTemplateType(modelIn.getTemplateType());
		// Routers Master
		org.opennaas.extensions.vcpe.model.Router physicalRouterMaster = getRouterOpennaas(modelIn.getPhysicalRouterMaster());
		physicalRouterMaster.setTemplateName(SPTemplateConstants.CPE1_PHY_ROUTER);
		// Routers Backup
		org.opennaas.extensions.vcpe.model.Router physicalRouterBackup = getRouterOpennaas(modelIn.getPhysicalRouterBackup());
		physicalRouterBackup.setTemplateName(SPTemplateConstants.CPE2_PHY_ROUTER);
		// Routers Core
		org.opennaas.extensions.vcpe.model.Router physicalRouterCore = getRouterOpennaas(modelIn.getPhysicalRouterBackup());
		physicalRouterCore.setTemplateName(SPTemplateConstants.CORE_PHY_ROUTER);

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
	private static org.opennaas.extensions.vcpe.model.Router getRouterOpennaas(Router phyRouterIn) {
		org.opennaas.extensions.vcpe.model.Router phyRouterOut = new org.opennaas.extensions.vcpe.model.Router();
		if (phyRouterIn != null) {
			phyRouterOut.setName(phyRouterIn.getName());
			phyRouterOut.setTemplateName(phyRouterIn.getTemplateName());
			// Interfaces
			List<org.opennaas.extensions.vcpe.model.Interface> interfaces = new ArrayList<org.opennaas.extensions.vcpe.model.Interface>();
			phyRouterOut.setInterfaces(interfaces);
			for (int i = 0; i < phyRouterIn.getInterfaces().size(); i++) {
				org.opennaas.extensions.vcpe.model.Interface inter = getInterfaceOpennaas(phyRouterIn.getInterfaces().get(i));
				interfaces.add(inter);
			}
		}
		return phyRouterOut;
	}

	/**
	 * Return a OpenNaaS logical interface from a GUI interface<br>
	 * If is a physical interface -> port = 0 , vlan = 0 and name = physicalInterfaceName
	 * 
	 * @param interface
	 * @return Interface
	 */
	private static org.opennaas.extensions.vcpe.model.Interface getInterfaceOpennaas(Interface inIface) {
		org.opennaas.extensions.vcpe.model.Interface outIface = new org.opennaas.extensions.vcpe.model.Interface();
		// If physical interface name = physicalInterfaceName
		outIface.setName(inIface.getCompleteName());
		outIface.setIpAddress(inIface.getIpAddress());
		outIface.setVlan(inIface.getVlan() != null ? inIface.getVlan() : 0);
		outIface.setTemplateName(inIface.getTemplateName());
		outIface.setPhysicalInterfaceName(inIface.getName());
		outIface.setPort(inIface.getPort() != null ? Integer.parseInt(inIface.getPort()) : 0);
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
