/**
 * 
 */
package org.opennaas.web.bos;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.web.entities.LogicalRouter;
import org.opennaas.web.entities.VCPENetwork;
import org.opennaas.web.services.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jordi
 */
public class VCPENetworkBO {

	private static final Logger	LOGGER			= Logger.getLogger(VCPENetworkBO.class);
	private static final String	RESOURCE_TYPE	= "vcpenet";
	private static final String	RESOURCE_NAME	= "vCPENet-1";

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
		// return vcpeNetworkDao.getById(vcpeNetworkId);
		return getAll().get(0);
	}

	/**
	 * Get all VCPE Network
	 * 
	 * @return List<VCPENetwork>
	 */
	public List<VCPENetwork> getAll() {
		LOGGER.debug("get all VCPENetwork");
		// TODO Need to call OpenNaaS through DAO layer
		// return vcpeNetworkDao.getAll();
		List<VCPENetwork> list = new ArrayList<VCPENetwork>();
		VCPENetwork vcpeNetwork1 = new VCPENetwork();
		VCPENetwork vcpeNetwork2 = new VCPENetwork();

		vcpeNetwork1.setId("1");
		vcpeNetwork1.setName("VCPENetwork-1");

		LogicalRouter logicalRouter1 = new LogicalRouter();
		logicalRouter1.setName("LR1-VCPE1");

		LogicalRouter logicalRouter2 = new LogicalRouter();
		logicalRouter2.setName("LR2-VCPE1");

		vcpeNetwork1.setLogicalRouter1(logicalRouter1);
		vcpeNetwork1.setLogicalRouter2(logicalRouter2);

		vcpeNetwork2.setId("2");
		vcpeNetwork2.setName("VCPENetwork-2");

		logicalRouter1 = new LogicalRouter();
		logicalRouter1.setName("LR1-VCPE2");

		logicalRouter2 = new LogicalRouter();
		logicalRouter2.setName("LR2-VCPE2");

		vcpeNetwork2.setLogicalRouter1(logicalRouter1);
		vcpeNetwork2.setLogicalRouter2(logicalRouter2);

		list.add(vcpeNetwork1);
		list.add(vcpeNetwork2);

		return list;
	}

	/**
	 * 
	 * @param params
	 * @return
	 */
	private ResourceDescriptor getResourceDescriptor(VCPENetwork vcpeNetwork) {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		Information information = new Information();
		information.setType(RESOURCE_TYPE);
		information.setType(RESOURCE_NAME);
		resourceDescriptor.setInformation(information);
		return resourceDescriptor;
	}
}
