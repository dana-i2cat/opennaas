package org.opennaas.extensions.sampleresource.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ResourceRepository;
import org.opennaas.core.resources.capability.ICapabilityFactory;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class SampleResourceRepository extends ResourceRepository {

	/**
	 * Logger
	 */
	Log	log	= LogFactory.getLog(SampleResourceRepository.class);

	/**
	 * @param resourceType
	 */
	public SampleResourceRepository(String resourceType) {
		super(resourceType);
	}

	/**
	 * Add a factory to the capability factories
	 * 
	 * @param capabilityFactory
	 */
	public void capabilityFactoryAdded(ICapabilityFactory capabilityFactory) {
		log.info("Adding factory: " + capabilityFactory.getType());
		this.capabilityFactories.put(capabilityFactory.getType(), capabilityFactory);
	}

	/**
	 * Remove a factory from the capability factories
	 * 
	 * @param capabilityFactory
	 */
	public void capabilityFactoryDeleted(ICapabilityFactory capabilityFactory) {
		log.info("Deleting factory: " + capabilityFactory.getType());
		this.capabilityFactories.remove(capabilityFactory.getType());
	}
}
