package org.opennaas.extensions.pdu.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.pdu.model.PDUModel;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class PDUResourceBootstrapper implements IResourceBootstrapper {

	Log				log	= LogFactory.getLog(PDUResourceBootstrapper.class);

	private IModel	oldModel;

	@Override
	public void bootstrap(Resource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");
		resource.setModel(new PDUModel());
		// Add here all the necessary methods to populate resource model
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {

		resource.setModel(new PDUModel());
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		resource.setModel(oldModel);
	}

}
