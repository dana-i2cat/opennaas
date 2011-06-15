package net.i2cat.mantychore.repository;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceBootstrapper;
import net.i2cat.nexus.resources.ResourceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MantychoreBootstrapper implements IResourceBootstrapper {
	Log	log	= LogFactory.getLog(MantychoreRepository.class);

	@Override
	public void bootstrap(IResource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource..");
		resource.setModel(new ComputerSystem());

		if (resource.getProfile() != null) {
			resource.getProfile().initModel(resource.getModel());
		}

	}

	@Override
	public void revertBootstrap(IResource resource) throws ResourceException {
		resource.setModel(null);
	}

}
