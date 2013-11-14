package org.opennaas.extensions.vrf.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.vrf.model.VRFModel;

/**
 * 
 * @author Josep Batallé (josep.batalle@i2cat.net)
 * 
 */
public class VRFBootstrapper implements IResourceBootstrapper {

	Log log	= LogFactory.getLog(VRFBootstrapper.class);

	private IModel	oldModel;

	@Override
	public void bootstrap(Resource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");
		resource.setModel(new VRFModel());
		// Add here all the necessary methods to populate resource model
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {

		resource.setModel(new VRFModel());
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		resource.setModel(oldModel);
	}

}
