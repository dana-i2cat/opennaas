package org.opennaas.extensions.rfv.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.rfv.model.RFVModel;

/**
 * 
 * @author Josep Batallé (josep.batalle@i2cat.net)
 * 
 */
public class RFVBootstrapper implements IResourceBootstrapper {

	Log log	= LogFactory.getLog(RFVBootstrapper.class);

	private IModel	oldModel;

	@Override
	public void bootstrap(Resource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");
		resource.setModel(new RFVModel());
		// Add here all the necessary methods to populate resource model
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {

		resource.setModel(new RFVModel());
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		resource.setModel(oldModel);
	}

}
