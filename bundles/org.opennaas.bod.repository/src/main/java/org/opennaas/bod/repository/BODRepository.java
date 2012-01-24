package org.opennaas.bod.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ResourceRepository;

public class BODRepository extends ResourceRepository {
	Log	log	= LogFactory.getLog(BODRepository.class);

	public BODRepository(String resourceType, String persistenceUnit) {
		super(resourceType, persistenceUnit);
	}
}
