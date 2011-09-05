package net.i2cat.luminis.ROADM.repository;

import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.ResourceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ROADMBootstrapper implements IResourceBootstrapper {
	Log	log	= LogFactory.getLog(ROADMBootstrapper.class);

	@Override
	public void bootstrap(IResource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource..");
		resource.setModel(new ProteusOpticalSwitch()); // TODO LUMINIS WORK WITH OPTICAL SWITCHES OR WITH PROTEUS OPTICAL SWITCHES

		// set name in model
		// FIXME do this in profile.initModel
		((ProteusOpticalSwitch) resource.getModel()).setName(resource.getResourceDescriptor().getInformation().getName());

		if (resource.getProfile() != null) {
			resource.getProfile().initModel(resource.getModel());
		}

	}

	@Override
	public void revertBootstrap(IResource resource) throws ResourceException {
		resource.setModel(null);
	}

}
