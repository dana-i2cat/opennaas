/**
 * 
 */
package org.opennaas.web.bos;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.LogicalRouter;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.web.entities.VCPENetwork;
import org.opennaas.web.services.ResourceService;
import org.opennaas.web.services.VCPENetworkService;
import org.opennaas.web.services.rest.RestServiceException;
import org.opennaas.web.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jordi
 */
public class VCPENetworkBO {

	private static final Logger	LOGGER	= Logger.getLogger(VCPENetworkBO.class);

	@Autowired
	private ResourceService		resourceService;

	@Autowired
	private VCPENetworkService	vcpeNetworkService;

	/**
	 * Create a VCPE Network, start the resource and build the enviroment
	 * 
	 * @param vcpeNetwork
	 * @throws RestServiceException
	 */
	public String create(VCPENetwork vcpeNetwork) throws RestServiceException {
		LOGGER.debug("create a VCPENetwork: " + vcpeNetwork);
		String vcpeNetworkId = resourceService.createResource(getResourceDescriptor(vcpeNetwork));
		LOGGER.debug("start the VCPENetwork with id: " + vcpeNetworkId);
		// resourceService.startResource(vcpeNetworkId);
		LOGGER.debug("build the VCPENetwork enviroment");
		vcpeNetworkService.createVCPENetwork(getCreateVCPERequest(vcpeNetworkId, vcpeNetwork));
		return vcpeNetworkId;
	}

	/**
	 * Delete a VCPE Network. First stop the resource
	 * 
	 * @param vcpeNetworkId
	 * @throws RestServiceException
	 */
	public void delete(String vcpeNetworkId) throws RestServiceException {
		LOGGER.debug("stop a VCPENetwork with id: " + vcpeNetworkId);
		resourceService.stopResource(vcpeNetworkId);
		LOGGER.debug("delete a VCPENetwork with id: " + vcpeNetworkId);
		resourceService.deleteResource(vcpeNetworkId);
	}

	/**
	 * Get a VCPE Network with id = vcpeNetworkId
	 * 
	 * @param vcpeNetworkId
	 * @return VCPENetwork
	 */
	public VCPENetwork getById(String vcpeNetworkId) {
		LOGGER.debug("get a VCPENetwork with id: " + vcpeNetworkId);
		// TODO Call to OpenNaaS
		// Resource resource = resourceService.getResourceById(vcpeNetworkId);
		// return resourceToVCPENetwork(resource);
		VCPENetwork vcpeNetwork = new VCPENetwork();
		vcpeNetwork.setName("VCPENetwork-1");
		return vcpeNetwork;
	}

	/**
	 * Get all VCPE Network
	 * 
	 * @return List<VCPENetwork>
	 */
	public List<VCPENetwork> getAll() {
		LOGGER.debug("get all VCPENetwork");
		List<VCPENetwork> vcpeNetworks = new ArrayList<VCPENetwork>();
		VCPENetwork vcpeNetwork1 = new VCPENetwork();
		VCPENetwork vcpeNetwork2 = new VCPENetwork();
		vcpeNetworks.add(vcpeNetwork1);
		vcpeNetworks.add(vcpeNetwork2);
		vcpeNetwork1.setName("VCPENetwork-1");
		vcpeNetwork1.setId("1");
		vcpeNetwork2.setName("VCPENetwork-2");
		vcpeNetwork2.setId("2");
		return vcpeNetworks;
		// TODO Call to OpenNaaS
		// return resourceService.getAllResources();
	}

	/**
	 * Get the descriptor with his capability to create the resource
	 * 
	 * @param params
	 * @return VCPENetworkDescriptor
	 */
	private VCPENetworkDescriptor getResourceDescriptor(VCPENetwork vcpeNetwork) {
		VCPENetworkDescriptor descriptor = new VCPENetworkDescriptor();
		Information information = new Information();
		information.setType(Constants.RESOURCE_VCPENET_TYPE);
		information.setName(vcpeNetwork.getName());
		descriptor.setInformation(information);
		// Capability
		List<CapabilityDescriptor> capabs = new ArrayList<CapabilityDescriptor>();
		capabs.add(getBuilderCapability());
		descriptor.setCapabilityDescriptors(capabs);
		return descriptor;
	}

	/**
	 * Get the builder capability of the VCPENetwork to create the resource
	 * 
	 * @return CapabilityDescriptor
	 */
	private CapabilityDescriptor getBuilderCapability() {
		CapabilityDescriptor desc = new CapabilityDescriptor();
		Information info = new Information();
		info.setType(Constants.CAPABILITY_VCPENET_BUILDER);
		desc.setCapabilityInformation(info);
		return desc;
	}

	/**
	 * Get params to call the ws to create the VCPENetwork enviroment
	 * 
	 * @param vcpeNetworkId
	 * 
	 * @param vcpeNetwork
	 * @return CreateVCPENetRequest
	 */
	private VCPENetworkModel getCreateVCPERequest(String vcpeNetworkName, VCPENetwork vcpeNetwork) {
		VCPENetworkModel request = new VCPENetworkModel();
		// Id
		request.setVcpeNetworkName(vcpeNetworkName);
		// Elements
		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		request.setElements(elements);
		// LogicalRouters
		LogicalRouter logicalRouter1 = getLogicalRouter(vcpeNetwork.getLogicalRouter1().getName(), vcpeNetwork.getLogicalRouter1().getInterfaces());
		LogicalRouter logicalRouter2 = getLogicalRouter(vcpeNetwork.getLogicalRouter2().getName(), vcpeNetwork.getLogicalRouter2().getInterfaces());
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
	 * @param string
	 * 
	 * @param logicalRouter1
	 * @return
	 */
	private LogicalRouter getLogicalRouter(String lrName, List<org.opennaas.web.entities.Interface> ifaces) {
		LogicalRouter router = new LogicalRouter();
		router.setName(lrName);
		router.setNameInTemplate(lrName);
		// Interfaces
		List<Interface> interfaces = new ArrayList<Interface>();
		router.setInterfaces(interfaces);
		// Interface Inter
		interfaces.add(getInterface(lrName + "-inter", ifaces.get(0)));
		// Interface Down
		interfaces.add(getInterface(lrName + "-down", ifaces.get(1)));
		// Interface Up
		interfaces.add(getInterface(lrName + "-up", ifaces.get(2)));
		return router;
	}

	/**
	 * Return a OpenNaaS interface from a GUI interface
	 * 
	 * @param interface1
	 * @return Interface
	 */
	private Interface getInterface(String name, org.opennaas.web.entities.Interface iface) {
		Interface inter = new Interface();
		inter.setName(iface.getName() + "." + iface.getPort());
		inter.setIpAddress(iface.getIpAddress());
		inter.setVlanId(iface.getVlan());
		inter.setNameInTemplate(name);
		return inter;
	}
}
