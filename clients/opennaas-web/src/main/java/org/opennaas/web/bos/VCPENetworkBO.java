/**
 * 
 */
package org.opennaas.web.bos;

import java.util.List;

import org.apache.log4j.Logger;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;
import org.opennaas.web.entities.VCPENetwork;
import org.opennaas.web.services.ResourceService;
import org.opennaas.web.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jordi
 */
public class VCPENetworkBO {

	private static final Logger	LOGGER	= Logger.getLogger(VCPENetworkBO.class);

	@Autowired
	private ResourceService		resourceService;

	/**
	 * Create a VCPE Network. After start the resource
	 * 
	 * @param vcpeNetwork
	 */
	public String create(VCPENetwork vcpeNetwork) {
		LOGGER.debug("create a VCPENetwork: " + vcpeNetwork);
		String vcpeNetworkId = resourceService.create(getResourceDescriptor(vcpeNetwork));
		LOGGER.debug("start the VCPENetwork with id: " + vcpeNetworkId);
		resourceService.start(vcpeNetworkId);
		return vcpeNetworkId;
	}

	/**
	 * Delete a VCPE Network. First stop the resource
	 * 
	 * @param vcpeNetworkId
	 */
	public void delete(String vcpeNetworkId) {
		LOGGER.debug("stop a VCPENetwork with id: " + vcpeNetworkId);
		resourceService.stop(vcpeNetworkId);
		LOGGER.debug("delete a VCPENetwork with id: " + vcpeNetworkId);
		resourceService.delete(vcpeNetworkId);
	}

	/**
	 * Get a VCPE Network with id = vcpeNetworkId
	 * 
	 * @param vcpeNetworkId
	 * @return VCPENetwork
	 */
	public VCPENetwork getById(String vcpeNetworkId) {
		LOGGER.debug("get a VCPENetwork with id: " + vcpeNetworkId);
		// TODO Need to call OpenNaaS through DAO layer
		// return resourceService.getById(vcpeNetworkId);
		return getAll().get(0);
	}

	/**
	 * Get all VCPE Network
	 * 
	 * @return List<VCPENetwork>
	 */
	public List<VCPENetwork> getAll() {
		LOGGER.debug("get all VCPENetwork");
		return resourceService.getAll();
	}

	/**
	 * 
	 * @param params
	 * @return
	 */
	private VCPENetworkDescriptor getResourceDescriptor(VCPENetwork vcpeNetwork) {
		VCPENetworkDescriptor descriptor = new VCPENetworkDescriptor();
		Information information = new Information();
		information.setType(Constants.RESOURCE_VCPENET_TYPE);
		information.setName(vcpeNetwork.getName());
		descriptor.setInformation(information);
		return descriptor;
	}
}
