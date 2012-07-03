package org.opennaas.extensions.sampleresource.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.sampleresource.model.SampleModel;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class sampleResourceBootstrapper implements IResourceBootstrapper {

	Log				log	= LogFactory.getLog(sampleResourceBootstrapper.class);

	private IModel	oldModel;

	public void bootstrap(IResource resource) throws ResourceException {

		log.info("Loading bootstrap to start resource...");
		resource.setModel(new SampleModel());
		// Add here all the necessary methods to populate resource model
		//
	}

	@Override
	public void resetModel(IResource resource) throws ResourceException {

		resource.setModel(new SampleModel());
	}

	@Override
	public void revertBootstrap(IResource resource) throws ResourceException {

		resource.setModel(oldModel);
	}

}
