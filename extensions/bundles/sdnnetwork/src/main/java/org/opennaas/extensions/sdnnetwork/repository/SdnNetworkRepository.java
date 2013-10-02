package org.opennaas.extensions.sdnnetwork.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ResourceRepository;
import org.opennaas.core.resources.capability.ICapabilityFactory;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class SdnNetworkRepository extends ResourceRepository {

	public static final String	SDN_NETWORK_RESOURCE_TYPE	= "sdnnetwork";

	Log							log							= LogFactory.getLog(SdnNetworkRepository.class);

	public SdnNetworkRepository(String resourceType) {
		super(resourceType);
	}

	public void capabilityFactoryAdded(ICapabilityFactory capabilityFactory) {
		log.info("Adding factory: " + capabilityFactory.getType());
		this.capabilityFactories.put(capabilityFactory.getType(), capabilityFactory);
	}

	public void capabilityFactoryDeleted(ICapabilityFactory capabilityFactory) {
		if (capabilityFactory != null) {
			log.info("Deleting factory: " + capabilityFactory.getType());
			this.capabilityFactories.remove(capabilityFactory.getType());
		}
	}

}
