package org.opennaas.extensions.sdnnetwork.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkModel;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
public class SdnNetworkBootstrapper implements IResourceBootstrapper {
	
	Log log	= LogFactory.getLog(SdnNetworkBootstrapper.class);
	
	private IModel	oldModel;

	@Override
	public void bootstrap(Resource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");
		oldModel = resource.getModel();
		resource.setModel(new SDNNetworkModel());

	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {
		resource.setModel(oldModel);
	}

	/**
	 * Since we're not going to read information from real switches, we would not implement the reset model method,
	 * so no one can delete the modelinformation. Model will be reset only during bootstrap.
	 * 
	 */
	@Override
	public void resetModel(Resource resource) throws ResourceException {
		log.info("Ignoring resetModel signal.");
	}

}
