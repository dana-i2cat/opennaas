package org.opennaas.extensions.quantum.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.quantum.model.QuantumModel;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class QuantumResourceBootstrapper implements IResourceBootstrapper {

	Log				log	= LogFactory.getLog(QuantumResourceBootstrapper.class);

	private IModel	oldModel;

	@Override
	public void bootstrap(Resource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");
		resource.setModel(new QuantumModel());
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {
		resource.setModel(new QuantumModel());
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {
		resource.setModel(oldModel);
	}

}
