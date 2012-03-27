package org.opennaas.extensions.network.repository;

import org.opennaas.core.resources.ResourceRepository;
import org.opennaas.core.resources.capability.ICapabilityFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NetworkRepository extends ResourceRepository {
	Log	log	= LogFactory.getLog(NetworkRepository.class);

	public NetworkRepository(String resourceType) {
		super(resourceType);
	}

	public void capabilityFactoryAdded(ICapabilityFactory capabilityFactory) {
		log.info("Adding factory: " + capabilityFactory.getType());
		this.capabilityFactories.put(capabilityFactory.getType(), capabilityFactory);
	}

	public void capabilityFactoryDeleted(ICapabilityFactory capabilityFactory) {
		log.info("Deleting factory: " + capabilityFactory.getType());
		this.capabilityFactories.remove(capabilityFactory.getType());
	}

}
