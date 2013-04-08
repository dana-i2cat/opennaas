package org.opennaas.extensions.vnmapper.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.vnmapper.model.VNMapperModel;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class VNMapperResourceBootstrapper implements IResourceBootstrapper {

	Log				log	= LogFactory.getLog(VNMapperResourceBootstrapper.class);

	private IModel	oldModel;

	public void bootstrap(Resource resource) throws ResourceException {

		log.info("Loading bootstrap to start resource...");
		resource.setModel(new VNMapperModel());
		// Add here all the necessary methods to populate resource model
		//
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {

		resource.setModel(new VNMapperModel());
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		resource.setModel(oldModel);
	}

}
