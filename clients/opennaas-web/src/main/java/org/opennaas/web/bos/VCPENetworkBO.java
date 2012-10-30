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
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.web.entities.VCPENetwork;
import org.opennaas.web.services.rest.RestServiceException;
import org.opennaas.web.services.rest.resource.ResourceService;
import org.opennaas.web.services.rest.vcpe.BuilderCapabilityService;
import org.opennaas.web.services.rest.vcpe.VCPENetworkService;
import org.opennaas.web.utils.Constants;
import org.opennaas.web.utils.model.OpennasBeanUtils;
import org.opennaas.web.utils.model.VCPEBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jordi
 */
public class VCPENetworkBO {

	private static final Logger			LOGGER	= Logger.getLogger(VCPENetworkBO.class);

	@Autowired
	private ResourceService				resourceService;

	@Autowired
	private VCPENetworkService			vcpeNetworkService;

	@Autowired
	private BuilderCapabilityService	builderService;

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
		resourceService.startResource(vcpeNetworkId);
		LOGGER.debug("create the VCPENetwork enviroment");
		vcpeNetworkService.buildVCPENetwork(OpennasBeanUtils.getVCPENetwork(vcpeNetworkId, vcpeNetwork));
		return vcpeNetworkId;
	}

	/**
	 * Delete a VCPE Network. First stop the resource
	 * 
	 * @param vcpeNetworkId
	 * @throws RestServiceException
	 */
	public void delete(String vcpeNetworkId) throws RestServiceException {
		LOGGER.debug("destroy the VCPENetwork enviroment");
		vcpeNetworkService.destroyVCPENetwork(vcpeNetworkId);
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
	 * @throws RestServiceException
	 */
	public VCPENetwork getById(String vcpeNetworkId) throws RestServiceException {
		LOGGER.debug("get a VCPENetwork with id: " + vcpeNetworkId);
		VCPENetworkModel openNaasModel = vcpeNetworkService.getVCPENetworkById(vcpeNetworkId);
		return VCPEBeanUtils.getVCPENetwork(openNaasModel);
	}

	/**
	 * Get all VCPE Network
	 * 
	 * @return List<VCPENetwork>
	 * @throws RestServiceException
	 */
	public List<VCPENetwork> getAllVCPENetworks() throws RestServiceException {
		LOGGER.debug("get all VCPENetwork");
		return getListVCPENetworkGUI(vcpeNetworkService.getAllVCPENetworks());
	}

	/**
	 * Update the ip's of the VCPENetwork
	 * 
	 * @param vcpeNetwork
	 * @return true if the Ips have been updated
	 */
	public Boolean updateIps(VCPENetwork vcpeNetwork) throws RestServiceException {
		LOGGER.debug("update Ip's of VCPENetwork");
		builderService.updateIpsVCPENetwork(OpennasBeanUtils.getVCPENetwork(vcpeNetwork.getId(), vcpeNetwork));
		return true;
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
	 * @param allVCPENetworks
	 * @return
	 */
	private List<VCPENetwork> getListVCPENetworkGUI(List<VCPENetworkModel> listModelIn) {
		List<VCPENetwork> listModelOut = new ArrayList<VCPENetwork>();
		for (int i = 0; i < listModelIn.size(); i++) {
			listModelOut.add(VCPEBeanUtils.getVCPENetwork(listModelIn.get(i)));
		}
		return listModelOut;
	}

}
