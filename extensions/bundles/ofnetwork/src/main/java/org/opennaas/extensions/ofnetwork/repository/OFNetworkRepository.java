package org.opennaas.extensions.ofnetwork.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ResourceRepository;
import org.opennaas.core.resources.capability.ICapabilityFactory;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class OFNetworkRepository extends ResourceRepository {

	public static final String	OF_NETWORK_RESOURCE_TYPE	= "ofnetwork";

	Log							log							= LogFactory.getLog(OFNetworkRepository.class);

	public OFNetworkRepository(String resourceType) {
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
