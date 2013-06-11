package org.opennaas.extensions.powernet.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.powernet.model.PowerNetModel;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class PowerNetResourceBootstrapper implements IResourceBootstrapper {

	Log				log	= LogFactory.getLog(PowerNetResourceBootstrapper.class);

	private IModel	oldModel;

	@Override
	public void bootstrap(Resource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");
		resource.setModel(new PowerNetModel());
		// Add here all the necessary methods to populate resource model
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {

		resource.setModel(new PowerNetModel());
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		resource.setModel(oldModel);
	}

}
